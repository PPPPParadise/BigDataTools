import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.*;

public class TFIDFChainJobs{
    private static ArrayList<StringDoublePair> unsorted = new ArrayList<>();
    public static class WordFreqMapper extends Mapper<Object, Text, Text, IntWritable>{
        public void map(Object key, Text value, Context context)
                throws IOException, InterruptedException {

            StringTokenizer itr = new StringTokenizer(value.toString(),
                    "\\\f\\\r\\\b\\\\\n1234567890&=\'\"\\\t,;\\.\\?!:@\\[\\]\\(\\)\\{\\}_\\*/\\\\ ");
            while (itr.hasMoreTokens()) {
                // get doc name and convert to Text
                String docNameStr = ((FileSplit)context.getInputSplit()).getPath().toString();

                context.write(new Text(itr.nextToken().toLowerCase() + " " + docNameStr), new IntWritable(1));
            }
        }
    }

    public static class WordFreqReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
        public void reduce(Text wordDocPair, Iterable<IntWritable> values, Context context)
                throws IOException, InterruptedException {

            int sum = 0;
            for (IntWritable val : values) {
                sum += val.get();
            }
            context.write(wordDocPair,new IntWritable(sum));
        }
    }

    public static class WordCountPerDocMapper extends Mapper<LongWritable, Text, Text, Text>{
        public void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {
                // Split the result from the previous reducer into word-doc-counts
                String[] elements = value.toString().split("\\s+");

                context.write(new Text(elements[1]), new Text(elements[0] + " " + elements[2]));
            }
    }

    public static class WordCountPerDocReducer extends Reducer<Text, Text, Text, Text> {
        public void reduce(Text docName, Iterable<Text> values, Context context)
                throws IOException, InterruptedException {

            int sum = 0; // total number of words in a document
            ArrayList<String[]> wordCountPairs = new ArrayList<>(); // used to store the word-wordCount pairs
            for (Text val : values) {
                // Split the values passed from mappers to word-wordCount pairs
                String[] wordCount = val.toString().split(" ");
                wordCountPairs.add(wordCount);
                sum += Integer.parseInt(wordCount[1]); // get the total number of word
            }

            for (String[] wordC : wordCountPairs) {
                String wordDoc = wordC[0] + " " + docName.toString() ;
                String countTotal = wordC[1] + " " + sum;
                context.write(new Text(wordDoc), new Text(countTotal));
            }
        }
    }

    public static class TfIdfMapper extends Mapper<LongWritable, Text, Text, Text>{
        public void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {
            String[] elements = value.toString().split("\\s+");

            context.write(new Text(elements[0]), new Text(elements[1] + " " + elements[2] + " " + elements[3]));
        }
    }

    public static class TfIdfReducer extends Reducer<Text, Text, Text, DoubleWritable> {
        public void reduce(Text word, Iterable<Text> values, Context context)
                throws IOException, InterruptedException {
            int docNumber = context.getConfiguration().getInt("fileCounts",1);
            int docWord = 0;
            ArrayList<String[]> valueList = new ArrayList<>(); // used to store the word-wordCount pairs
            for (Text val : values) {
                docWord++;
                valueList.add(val.toString().split(" "));
            }
            for (String[] vals : valueList) {
                double wordCount = Double.parseDouble(vals[1]);
                double wordsDoc = Double.parseDouble(vals[2]);
                double tfIdf = (wordCount/wordsDoc)*Math.log((double)docNumber / (double)(docWord));

                String wordDoc = word.toString() + " " + vals[0];
                unsorted.add(new StringDoublePair(wordDoc,tfIdf));
                context.write(new Text(wordDoc),new DoubleWritable(tfIdf));
            }
        }
    }

    private static class StringDoublePair{
        String s;
        double d;
        StringDoublePair(String s, double d){
            this.s = s;
            this.d = d;
        }
        public String toString(){
            return s + " " + d;
        }
    }

    static class SortbyDouble implements Comparator<StringDoublePair>{
        public int compare(StringDoublePair a, StringDoublePair b){
            double divid = a.d - b.d;
            if(divid < 0.0){
                return 1;
            }
            else if(divid == 0.0) {
                return 0;
            }else{
                return -1;
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();

        Path inputPath = new Path(args[0]);
        FileSystem fs = inputPath.getFileSystem(conf);
        FileStatus[] stat = fs.listStatus(inputPath);
        conf.setInt("fileCounts",stat.length);

        /**
         * Round 1
         */
        Job job = Job.getInstance(conf, "WordFreq");
        job.setJarByClass(TFIDFChainJobs.class);

        job.setMapperClass(WordFreqMapper.class);
        job.setCombinerClass(WordFreqReducer.class);
        job.setReducerClass(WordFreqReducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path("intermedia1"));

        job.waitForCompletion(true);

        /**
        * Round 2
        */
        Job job2 = Job.getInstance(conf, "WordCountPerDoc");
        job2.setJarByClass(TFIDFChainJobs.class);

        job2.setMapperClass(WordCountPerDocMapper.class);
        job2.setReducerClass(WordCountPerDocReducer.class);

        job2.setOutputKeyClass(Text.class);
        job2.setOutputValueClass(Text.class);

        FileInputFormat.addInputPath(job2, new Path("intermedia1"));
        FileOutputFormat.setOutputPath(job2, new Path("intermedia2"));

        job2.waitForCompletion(true);

        /**
         * Round 3
         */
        Job job3 = Job.getInstance(conf, "Round3");
        job3.setJarByClass(TFIDFChainJobs.class);

        job3.setMapperClass(TfIdfMapper.class);
        job3.setReducerClass(TfIdfReducer.class);

        job3.setMapOutputKeyClass(Text.class);
        job3.setMapOutputValueClass(Text.class);
        job3.setOutputKeyClass(Text.class);
        job3.setOutputValueClass(DoubleWritable.class);

        FileInputFormat.addInputPath(job3, new Path("intermedia2"));
        FileOutputFormat.setOutputPath(job3, new Path(args[1]));

        if(job3.waitForCompletion(true)) {
            Collections.sort(unsorted, new SortbyDouble());
            for (int i = 0; i < 20 ; i++) {
                System.out.println(unsorted.get(i));
            }
            System.exit(0);
        }
        System.exit(job3.waitForCompletion(true) ? 0 : 1);
    }
}

