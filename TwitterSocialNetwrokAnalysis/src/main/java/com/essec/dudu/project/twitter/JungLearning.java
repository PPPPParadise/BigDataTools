package com.essec.dudu.project.twitter;

/**
 * Created by dudu on 17/10/27.
 */
import java.awt.Dimension;
import java.util.*;
import javax.swing.JFrame;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.UndirectedOrderedSparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.VisualizationImageServer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;

public class JungLearning {

    public static void main(String[] args) {
        UndirectedOrderedSparseMultigraph g = new UndirectedOrderedSparseMultigraph();

        String[] keys = {"Walmart","Target","Amazon","Microsoft","Apple","Google","Disney","Nike","Starbucks","eBay","Facebook"};
        Map<Long,String> textMap = new HashMap<Long,String>();
        for(String key : keys){
            g.addVertex(key);
            List<TweetDO> tweetDOList = TweetDAO.getInstance().queryByDateAndText("2017-11-02",key);
            for(TweetDO tweetDO:tweetDOList){
                Long id = tweetDO.getIdTweet();
                if(!textMap.containsKey(id)){
                    textMap.put(id,key);
                }else{
                    textMap.put(id,textMap.get(id)+","+key);
                }
            }
        }

        Map<String,Double> edgeMap = new HashMap<String, Double>();
        for(Map.Entry<Long,String> entry:textMap.entrySet()){
            String[] values = entry.getValue().split(",");
            int len = values.length;
            for(int i=0;i<len;i++){
                for(int j=i+1;j<len;j++){
                    String edgeStr = values[i]+","+values[j];
                    if(!edgeMap.containsKey(edgeStr)){
                        edgeMap.put(edgeStr,1.0);
                    }else{
                        edgeMap.put(edgeStr,edgeMap.get(edgeStr)+1.0);
                    }
                }
            }

        }

        for(String vertex:keys){
            g.addVertex(vertex);
        }

        int i = 0;
        for(String edge:edgeMap.keySet()){
            g.addEdge(i++,edge.split(",")[0],edge.split(",")[1], EdgeType.UNDIRECTED);
        }

        List<UserDO> userDOList = UserDAO.getInstance().queryByDate("2017-11-02");

        VisualizationImageServer vs =
                new VisualizationImageServer(
                        new CircleLayout(g), new Dimension(600, 600)
                );
        vs.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
        JFrame frame = new JFrame();
        frame.getContentPane().add(vs);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
