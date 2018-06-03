package cs.bigdata.Tutorial2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class History {
	public static void main(String[] args) throws IOException {
		//localSrc when not used within Hadoop
		//String localSrc = System.getProperty("user.dir") + "/isd-history.txt";
		
		String localSrc = args[0];
		Path path = new Path(localSrc);
		//Open the file
		Configuration config = new Configuration();
		FileSystem filesystem  = FileSystem.get(config);
		FSDataInputStream in = filesystem.open(path);
		
		try{
			System.out.println("USAF - Name of Station - Country - Elevation");
			
			InputStreamReader inputstreamreader = new InputStreamReader(in);
			BufferedReader br = new BufferedReader(inputstreamreader);
			
			//count the first lines
			int count = 0;
			
			// read line by line
			String line = br.readLine();
			
			//Pass the first 22 lines
			while(line != null && count < 22){
				count++;
				line = br.readLine();
			}

			while(line != null){
				// Process of the current interesting line
				System.out.println(line.substring(0, 6) + " - " + line.substring(13, 42) + " - " + line.substring(43, 45) + " - " + line.substring(74, 81));
				line = br.readLine();
			}
		}
		finally{
			//close the file
			in.close();
			filesystem.close();
		}
	}
}