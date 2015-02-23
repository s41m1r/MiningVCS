/**
 * 
 */
package util;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * @author Saimir Bala
 *
 */
class Tree
{
    class Node
    {
        String data;
        ArrayList<Node> children;

        public Node(String data)
        {
            this.data = data;
            children = new ArrayList<Node>();
        }

        public Node getChild(String data)
        {
            for(Node n : children)
                if(n.data.equals(data))
                    return n;

            return null;
        }

		@Override
      public String toString() {
	      return "Node [data=" + data + ", children=" + children + "]";
      }
    }

    private Node root;

    public Tree()
    {
        root = new Node("");
    }

    public boolean isEmpty()
    {
        return root==null;
    }

    public void add(String str)
    {
        Node current = root;
        StringTokenizer s = new StringTokenizer(str, "/");
        while(s.hasMoreElements())
        {
            str = (String)s.nextElement();
            Node child = current.getChild(str);
            if(child==null)
            {
                current.children.add(new Node(str));
                child = current.getChild(str);
            }
            current = child;
        }
    }

    public void print()
    {
        print(this.root, 0);
    }

    private void print(Node n, int level)
    {
        if(n==null)
            return;
        for(Node c : n.children)
        {
            System.out.print(level+" "+c.data + " ");
            print(c, level+1);
        }
    }

    public static void main(String[] args)
    {
        Tree t = new Tree();
        t.add("/templates");
        t.add("/slides");
        t.add("The World/Asia/Iran");
        t.add("/templates/checkliste_kostenplan.doc");
        t.add("The World/Asia/China");    // Even if you insert only this statement.
                                          // You get the desired output, 
                                          // As any string not found is inserted
        t.print();
    }
}