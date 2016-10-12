package server;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.thrift.TException;

import fileService.FileStore;
import fileService.RFile;
import fileService.RFileMetadata;
import fileService.Status;
import fileService.StatusReport;
import fileService.SystemException;
import server.constants.Constants;
import shared.FileProcessorI;

public class FileStoreHandler implements FileStore.Iface {

	private Map<String, Map<String, RFileMetadata>> ownerToRFileMap = null;
	private FileProcessorI fileProcessor = null;
	private static final String SERVER_FILE_URI_PAT = Constants.ROOT_DIR_NAME + File.separator + "%s-%s";

	public FileStoreHandler(FileProcessorI fileProcessorIn) {
		ownerToRFileMap = new HashMap<String, Map<String, RFileMetadata>>();
		fileProcessor = fileProcessorIn;
	}

	@Override
	public List<RFileMetadata> listOwnedFiles(String user) throws SystemException, TException {
		if (ownerToRFileMap.containsKey(user)) {
			return new ArrayList<RFileMetadata>(ownerToRFileMap.get(user).values());
		}
		throwSystemException("Owner does not exists.");
		return null;
	}

	@Override
	public StatusReport writeFile(RFile rFile) throws SystemException, TException {
		// TODO: Should use hash to check if the contents of the file have
		// changed ? That would save the file io in case resource is not
		// modified.
		Path filePath = Paths.get(rFile.getMeta().getFilename());
		String serverFileUri = String.format(SERVER_FILE_URI_PAT, rFile.getMeta().getOwner(), filePath.getFileName());
		if (ownerToRFileMap.get(rFile.getMeta().getOwner()) == null) {
			// New owner
			RFileMetadata meta = initializeMetadata(rFile);
			Map<String, RFileMetadata> newFilesList = new HashMap<String, RFileMetadata>();
			newFilesList.put(serverFileUri, meta);
			ownerToRFileMap.put(meta.getOwner(), newFilesList);
			fileProcessor.write(serverFileUri, rFile.getContent());
			return new StatusReport(Status.SUCCESSFUL);
		} else {
			Map<String, RFileMetadata> filesMap = ownerToRFileMap.get(rFile.getMeta().getOwner());
			if ((filesMap.get(serverFileUri) == null)) {
				// Existing owner, new file
				RFileMetadata meta = initializeMetadata(rFile);
				filesMap.put(serverFileUri, meta);
				ownerToRFileMap.put(meta.getOwner(), filesMap);
				fileProcessor.write(serverFileUri, rFile.getContent());
				return new StatusReport(Status.SUCCESSFUL);
			} else {
				// Existing owner, existing file.
				RFileMetadata existingRFileMetadata = filesMap.get(serverFileUri);
				existingRFileMetadata.setUpdated(System.currentTimeMillis());
				existingRFileMetadata.setVersion(existingRFileMetadata.getVersion() + 1);
				existingRFileMetadata.setContentLength(rFile.getContent().length());
				existingRFileMetadata.setContentHash(rFile.getMeta().getContentHash());
				filesMap.put(serverFileUri, existingRFileMetadata);
				ownerToRFileMap.put(existingRFileMetadata.getOwner(), filesMap);
				fileProcessor.write(serverFileUri, rFile.getContent());
				return new StatusReport(Status.SUCCESSFUL);
			}
		}
	}

	private RFileMetadata initializeMetadata(RFile rFile) {
		RFileMetadata meta = new RFileMetadata();
		meta.setCreated(System.currentTimeMillis());
		meta.setUpdated(System.currentTimeMillis());
		meta.setFilename(rFile.getMeta().getFilename());
		meta.setOwner(rFile.getMeta().getOwner());
		meta.setContentHash(rFile.getMeta().getContentHash());
		meta.setVersion(0);
		meta.setContentLength(rFile.getContent().length());
		return meta;
	}

	@Override
	public RFile readFile(String filename, String owner) throws SystemException, TException {
		String serverFileUri = String.format(SERVER_FILE_URI_PAT, owner, filename);
		if (ownerToRFileMap.get(owner) == null) {
			throwSystemException("Owner does not exists.");
		} else {
			Map<String, RFileMetadata> filesMap = ownerToRFileMap.get(owner);
			if (filesMap.get(serverFileUri) == null) {
				throwSystemException("File does not exists.");
			} else {
				RFile rFile = new RFile();
				rFile.setContent(fileProcessor.read(serverFileUri));
				rFile.setMeta(filesMap.get(serverFileUri));
				return rFile;
			}
		}
		return null;
	}

	private void throwSystemException(String message) throws SystemException {
		SystemException exception = new SystemException();
		exception.setMessage(message);
		throw exception;
	}
}
