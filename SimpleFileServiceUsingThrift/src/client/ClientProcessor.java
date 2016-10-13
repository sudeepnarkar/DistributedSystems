package client;

import java.util.List;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TJSONProtocol;
import org.apache.thrift.protocol.TList;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TType;
import org.apache.thrift.transport.TIOStreamTransport;
import org.apache.thrift.transport.TTransport;

import client.beans.InputParams;
import client.constants.CliArgs;
import client.constants.Operation;
import client.util.InputArgumentsValidator;
import fileService.FileStore;
import fileService.RFile;
import fileService.RFileMetadata;
import fileService.StatusReport;
import fileService.SystemException;
import shared.FileProcessorI;

/**
 * This class contains the logic make RPC calls based on the type of operation.
 * 
 * @author anandkulkarni
 *
 */
public class ClientProcessor {

	private HashGeneratorI hashGenerator = null;
	private FileProcessorI fileProcessor = null;
	private InputArgumentsValidator validator = null;

	public ClientProcessor(HashGeneratorI hashGeneratorIn, FileProcessorI fileProcessorIn) {
		super();
		this.hashGenerator = hashGeneratorIn;
		this.fileProcessor = fileProcessorIn;
		this.validator = new InputArgumentsValidator();
	}

	/**
	 * This method processes each client request.
	 * 
	 * @param client
	 *            FileStore Client instance used to delegate client request to
	 *            underlying RPC mechanism.
	 * @param inputParams
	 *            contains input parameters provided through command line.
	 */
	public void process(FileStore.Client client, InputParams inputParams) {
		try {
			if (validator.validateIfNull(CliArgs.OPERATION, inputParams.getOperation())) {
				TTransport iosTransport = new TIOStreamTransport(System.out);
				TProtocol jsonProtocol = new TJSONProtocol(iosTransport);
				switch (Operation.fromValue(inputParams.getOperation())) {
				case READ:
					try {
						if (validator.validateIfNull(CliArgs.FILENAME, inputParams.getFileName())
								&& validator.validateIfNull(CliArgs.USER, inputParams.getUser())) {
							RFile response = client.readFile(inputParams.getFileName(), inputParams.getUser());
							response.write(jsonProtocol);
						}
					} catch (SystemException exception) {
						exception.write(jsonProtocol);
					}
					break;
				case WRITE:
					if (validator.validateIfNull(CliArgs.FILENAME, inputParams.getFileName())
							&& validator.validateIfNull(CliArgs.USER, inputParams.getUser())) {
						String contents = fileProcessor.read(inputParams.getFileName());
						RFile rFile = new RFile();
						RFileMetadata meta = new RFileMetadata();
						meta.setFilename(inputParams.getFileName());
						meta.setOwner(inputParams.getUser());
						meta.setContentHash(hashGenerator.generate(contents));
						rFile.setContent(contents);
						rFile.setMeta(meta);
						StatusReport status = client.writeFile(rFile);
						status.write(jsonProtocol);
					}
					break;
				case LIST:
					try {
						if (validator.validateIfNull(CliArgs.USER, inputParams.getUser())) {
							List<RFileMetadata> rFileMetadata = client.listOwnedFiles(inputParams.getUser());
							jsonProtocol.writeListBegin(new TList(TType.STRUCT, rFileMetadata.size()));
							rFileMetadata.stream().forEach(metaFile -> {
								try {
									metaFile.write(jsonProtocol);
								} catch (TException exception) {
									System.err.println("Error occured while Converting the response into json format.");
									exception.printStackTrace();
									System.exit(1);
								}
							});
							jsonProtocol.writeListEnd();
						}
					} catch (SystemException exception) {
						exception.write(jsonProtocol);
					}
					break;
				}
			}
		} catch (TException exception) {
			System.err.print("Error occured while sending a message over the network.");
			exception.printStackTrace();
			System.exit(1);
		}
	}
}
