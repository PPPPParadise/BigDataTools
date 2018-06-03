import java.io.IOException;
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

public class PageRank{
    public static class PageRankInitialMapper extends Mapper<Object, Text, Text, Text>{
        private String numNodes;
        public void map(Object key, Text value, Context context)
                throws IOException, InterruptedException {
            String[] line = value.toString().split("\\s+");
            if(line[0].equals("#")){
                if(line[1].equals("Nodes:")){
                    numNodes = line[2];
                }
            }else if(line.length > 1 ){
                context.write(new Text(line[0]), new Text(line[1] + "," + 1.0 + "," + numNodes));
            }
        }
    }

    public static class PageRankReducer extends Reducer<Text, Text, Text, Text> {
        public void reduce(Text nodeIDText, Iterable<Text> values, Context context)
                throws IOException, InterruptedException {
            String outgoings = "";
            String pr = "";
            String numNodes ="";
            int outgoingLinks = 0;
            for(Text val : values) {
                String[] tokens = val.toString().split(",");
                outgoings += tokens[0] + " ";
                pr = tokens[1];
                numNodes = tokens[2];
                outgoingLinks ++;
            }
            context.write(nodeIDText, new Text(pr + "\t" + outgoings + "\t" + outgoingLinks + "\t" + numNodes));
        }
    }

    public static class PageRankProcessMapper extends Mapper<LongWritable, Text, Text, Text>{
        public void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {
            String[] elements = value.toString().split("\\\t+");
            if (elements.length > 3) {
                StringTokenizer itr = new StringTokenizer(elements[2], " ");
                while (itr.hasMoreTokens()) {
                    String token = itr.nextToken();
                    context.write(new Text(token), new Text(elements[0] + "," + elements[1] + "," + elements[3]+",!"));
                }
                context.write(new Text(elements[0]), new Text("N,0,1,!"));  // to ensure the pageRank will be calculated
                // for nodes which do not have incoming links
                context.write(new Text(elements[0]), new Text(elements[2] + "," + elements[3]+ "," + elements[4])); // to pass the outgoing links and number of nodes to reducers
            }
        }
    }

    public static class PageRankProcessReducer extends Reducer<Text, Text, Text, Text> {
        private String numNodes="";
        public void reduce(Text nodeID, Iterable<Text> values, Context context)
                throws IOException, InterruptedException {
            double sum = 0.0;
            String outgoings = "";
            String outgoingLinks = "";
            for (Text val : values) {
                String[] elements = val.toString().split(",");
                if(elements.length == 4){
                    if (!elements[0].equals("N")){
                        sum += Double.parseDouble(elements[1])/Double.parseDouble(elements[2]);
                    }
                }else{
                    outgoings = elements[0];
                    outgoingLinks = elements[1];
                    numNodes = elements[2];
                }
            }
            context.write(new Text(nodeID), new Text((0.15/(Double.parseDouble(numNodes)) + 0.85*(sum)) + "\t"
                        + outgoings  + "\t" + outgoingLinks + "\t" + numNodes));
        }
    }


    public static class sortMapper extends Mapper<LongWritable, Text, DoubleWritable, Text>{
        public void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {
            String[] elements = value.toString().split("\\\t+");
            context.write(new DoubleWritable(Double.parseDouble(elements[1])),new Text(elements[0]));
        }
    }

    public static class sortReducer extends Reducer<Text, Text, DoubleWritable, Text> {
        public void reduce(DoubleWritable pagerank, Iterable<Text> values, Context context)
                throws IOException, InterruptedException {
            for(Text val : values){
                context.write(pagerank,val);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();

        if (args.length != 3) {
            System.err.println("Usage: PageRank <in> <out> <iterations>");
            System.exit(2);
        }

        /**
         * Job 1
         */
        Job job = Job.getInstance(conf, "nodeInAndOut");
        job.setJarByClass(PageRank.class);

        job.setMapperClass(PageRankInitialMapper.class);
        job.setReducerClass(PageRankReducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass( Text.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path("intermedia/intermedia1"));

        job.waitForCompletion(true);

        int iter = Integer.parseInt(args[2]);
        int i;
        for (i = 0; i < iter; i++) {
            /**
             * Job 2
             */
            Job job2 = Job.getInstance(conf, "job2");
            job2.setJarByClass(PageRank.class);

            job2.setMapperClass(PageRankProcessMapper.class);
            job2.setReducerClass(PageRankProcessReducer.class);

            job2.setOutputKeyClass(Text.class);
            job2.setOutputValueClass(Text.class);

            FileInputFormat.addInputPath(job2, new Path("intermedia/intermedia" + (i + 1)));
//            if(i == iter - 1){
//                FileOutputFormat.setOutputPath(job2, new Path(args[1]));
//            }else {
                FileOutputFormat.setOutputPath(job2, new Path("intermedia/intermedia" + (i + 2)));
//            }
            job2.waitForCompletion(true);
        }

        /**
         * Job 3 Sort
         */
        Job job3 = Job.getInstance(conf, "job3");
        job3.setJarByClass(PageRank.class);

        job3.setMapperClass(sortMapper.class);
        job3.setReducerClass(sortReducer.class);

        job3.setOutputKeyClass(DoubleWritable.class);
        job3.setOutputValueClass(Text.class);
        job3.setSortComparatorClass(LongWritable.DecreasingComparator.class);

        FileInputFormat.addInputPath(job3, new Path("intermedia/intermedia" + (i + 1)));
        FileOutputFormat.setOutputPath(job3, new Path(args[1]));

        System.exit(job3.waitForCompletion(true) ? 0 : 1);

    }
}