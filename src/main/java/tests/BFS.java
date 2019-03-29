package tests;

import mcts.Node;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class BFS {

    public static void treeVisualisationBFS(Node rootNode, int move) {
        List<Node> queue = new ArrayList<>();
        queue.add(rootNode);

        PrintWriter writer = null;
        try {
            writer = new PrintWriter("Tree for MCTS move " + move + " and node " + rootNode.getId() + ".txt", "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        int flag = 0;
        while(!queue.isEmpty()) {
            Node node = queue.get(0);
            queue.remove(node);
            for (Node child : node.getChildrenExplored()) {
                if (child.getPlayedPlayouts() > 0) {
                    queue.add(child);
                    if (child.getWonPlayouts() < 0)
                        flag = 1;  // opponent
                    else
                        flag = 0;  // MCTS
                    writer.println(node.getId() + ";" + child.getId() + ";" + child.getWonPlayouts() + " / " + child.getPlayedPlayouts() + ";" + flag);
                }
            }
        }
        writer.close();



    }


}
