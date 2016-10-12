package shared;

public interface FileProcessorI {
	public String read(String fileName);

	public void write(String fileName, String contents);
}
