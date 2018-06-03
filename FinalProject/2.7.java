//这里package 路径记得不要复制了，你自己下个eclipse建下自己的package
//然后把注释改一下
package cs.bigdata.Tutorial2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

@SuppressWarnings("unused")
public class Tree {
	private float[] geolocation = new float[2];
	private int borough;
	private String kind;
	private String species;
	private String family;
	private int yearOfPlanting;
	private float height;
	private float circumference;
	private String address;
	private String commonName;
	private String variety;
	private int treeId;
	private String environmentName;
	
	public Tree(String line)
	{
		String[] splitLine = line.split(";");
		
		String[] geoLoc = splitLine[0].substring(1, splitLine[0].length() - 1).split(",");
		
		if (geoLoc.length == 2 && geoLoc[0].length != 0 && geoLoc[1].length != 0)
		{
			this.geolocation[0] = Float.valueOf(geoLoc[0]).floatValue();
			this.geolocation[1] = Float.valueOf(geoLoc[1]).floatValue();
		}
		
		if (splitLine[1].length != 0)
		{
			this.borough = Integer.valueOf(splitLine[1]).intValue();
		}
			
		this.kind = splitLine[2];
		
		this.species = splitLine[3];
		
		this.family = splitLine[4];
		
		if (splitLine[5].length != 0)
		{
			this.yearOfPlanting = Integer.valueOf(splitLine[5]).intValue();
		}
		
		if (splitLine[6].length != 0)
		{
			this.height = Float.valueOf(geoLoc[6]).floatValue();
		}
		
		if (splitLine[7].length != 0)
		{
			this.circumference = Float.valueOf(geoLoc[7]).floatValue();
		}
		
		this.address = splitLine[8];
		
		this.commonName = splitLine[9];
		
		this.variety = splitLine[10];
		
		if (splitLine[11].length != 0)
		{
			this.treeId = Integer.valueOf(splitLine[11]).intValue();
		}
		
		this.environmentName = splitLine[12];
	}
	
	public static void main(String[] args) throws IOException {
		//localSrc when not used within hadoop
		//String localSrc = System.getProperty("user.dir") + "/arbres.csv";
		
		String localSrc = args[0];
		Path path = new Path(localSrc);
		//Open the file
		Configuration config = new Configuration();
		FileSystem filesystem = FileSystem.get(config);
		FSDataInputStream in = filesystem.open(path);
		
		try{
			InputStreamReader inputstreamreader = new InputStreamReader(in);
			BufferedReader br = new BufferedReader(inputstreamreader);
			
			// read line by line
			String line = br.readLine();
			
			while (line != null){
				// Split the information in a line
				String[] splitLine = line.split(";");
						
				// If the year is missing, we will display NULL
				if (splitLine[5].length() == 0)
				{
					splitLine[5] = "NULL";
				}
				
				// If the height is missing, we will display NULL
				if (splitLine[6].length() == 0)
				{
					splitLine[6] = "NULL";
				}
				
				// Print Year - Height
				System.out.println(splitLine[5] + " - " + splitLine[6]);
				
				// go to the next line
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