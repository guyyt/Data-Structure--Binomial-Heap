import java.util.Arrays;
/**
 * BinomialHeap in Java
 *By Guy Yom Tov
 */
public class BinomialHeap {
	
	private HeapNode firstNode;//
	private int size;
	private HeapNode minNode;
	
	public int numberOfLinks;


	public BinomialHeap() { //new BinomialHeap Constructor.
		this.firstNode=null;
		this.size=0; // the size initialized to 0.
		this.minNode=null;
	}
	

	public BinomialHeap(HeapNode node, int size) {//new BinomialHeap Constructor with firstNode and known size 
		this.firstNode = node;
		this.size = size;
		this.minNode=null;
	}

	/**
	 * public boolean empty()
	 *
	 * precondition: none
	 * 
	 * The method returns true if and only if the heap is empty.
	 * 
	 * O(1)
	 */
	public boolean empty() {
		return (this.size == 0);
	}


	/**
	 * public void insert(int i)
	 *
	 * Insert value into the binamical heap
	 *
	 * O(logn)
	 */
	public void insert(int i) {
		assert (i >= 0);
		
			if (empty()==true) {// the inseted node will be the only node in the heap.
				this.firstNode = new HeapNode(i); 
				this.minNode = this.firstNode;
				this.size = 1;
			} else 
			{
				// melding the current heap with a new heap which consists only the inserted node.
				BinomialHeap newH = new BinomialHeap();
				newH.insert(i);
				this.meld(newH);
			}
	}
	

	/**
	 * public void deleteMin()
	 *
	 * Delete the minimum value of the binobical heap.
	 * O(logn)
	 */
	public void deleteMin() {
		assert (empty()==false);

		if (minNode.getDegree() == 0) {
			// We only need to detach the minimum node from the heap.
			if (firstNode == minNode && firstNode.getnextSibling() == null) {
				this.becomeEmpty(); // the deleted item is the only item in the heap (and the minimal).
			} 
			else { 
				this.firstNode = this.minNode.getnextSibling();
				this.minNode = this.firstNode;

				// Update minNode.
				HeapNode node = this.firstNode;
				while (node != null) {
					if (node.getKey() < this.minNode.getKey())
						this.minNode = node;
					node = node.getnextSibling();
				}
				this.size--; //decreasing the size by one due to the deleting.
			}
		}
		else // on case minNode.getDegree() > 0
		{
			// reverse pointers of minNode's maxDegreeChildren.
			
			HeapNode node = this.minNode.getmaxDegreeChild();
			HeapNode prev = null;
			while (node != null) {
				HeapNode nextSibling = node.getnextSibling();
				node.setnextSibling(prev);
				node.setParent(null);
				prev = node;
				node = nextSibling;
			}
			
			this.minNode.setmaxDegreeChild(prev);

			// Create a new heap consists the maxDegreeChildren of the minNode.
			int size = (int) (Math.pow(2, this.minNode.getKey()) - 1);
			BinomialHeap h = new BinomialHeap(prev, size);

			if (this.minNode == this.firstNode) {
				this.firstNode = this.firstNode.getnextSibling();
			} 
			else 
			{
				prev = null;
				node = this.firstNode;
				while (node != this.minNode) {
					prev = node;
					node = node.getnextSibling();
				}
				prev.setnextSibling(this.minNode.getnextSibling());
			}

			// Meld will update minNode
			this.minNode = null;
			this.size -= (h.size() + 1);
			this.meld(h);
		}
	}


	/**
	 * public int findMin()
	 *
	 * Return the minimum value
	 *
	 * O(1)
	 */
	public int findMin() {
		if (this.minNode==null) return -1;
		
          return this.minNode.getKey();


	}
	
	/**
	 * public void meld (BinomialHeap heap2)
	 *
	 * Meld the heap with heap2
	 *
	 * 
	 * O(logn)
	 */
	public void meld(BinomialHeap heap2) {
		
		this.firstNode = merge(heap2);
		this.size += heap2.size(); // after merging two heaps the size is the size of their sum.

		HeapNode prev = null;
		HeapNode curr = this.firstNode;
		HeapNode next;
		if(curr == null) next=null;
		else next=curr.getnextSibling();
		
		HeapNode min = this.firstNode;

		while (next != null) {
			if (min.getKey() > next.getKey()) {
				min = next;
			}
			if ((curr.getDegree() != next.getDegree())
			|| ((next.getnextSibling() != null) && (next.getnextSibling().getDegree() == next.getDegree()))) {
				// cannot link here, continue going right on heap
				prev = curr;
				curr = next;
			} else {
				//  need to link curr and next
				if (curr.getKey() <= next.getKey()) {
					curr.setnextSibling(next.getnextSibling());
					curr = link(curr, next);
				} else {
					if (prev == null)
						this.firstNode = next;
					else
						prev.setnextSibling(next);
					curr = link(next, curr);
				}
			}
			next = curr.getnextSibling();
		}
		if (curr != null && min.getKey() > curr.getKey())
			min = curr;

		this.minNode = min; // updating minNode
	}


	/**
	 * public int size()
	 *
	 * Return the number of elements in the binomical heap.
	 * 
	 * 
	 * O(1)
	 */
	public int size() {
		return this.size;
	}
	
	

	
	/**
	 * public int minTreeRank()
	 *
	 * Return the minimum rank of a tree in the heap.
	 * if there is no items in the heap, returns -1.
	 * 
	 * O(1)
	 */
	public int minTreeRank() {
			if (this.firstNode==null) return -1;
			
			return this.firstNode.getDegree();
		
	}
	
	/**
	 * public boolean[] binaryRep()
	 *
	 * Return an array contains the binary representation of the heap.
	 * 
	 * O(logn)
	 */
	public boolean[] binaryRep() {

		boolean[] arry;
		if (empty()==true)
			arry = new boolean[1];
		else {
			String str = Integer.toString(this.size, 2);
			arry = new boolean[str.length()];
			
			for (int i = 0; i < str.length(); i++)
				if (str.charAt(i) == '1')
					arry[i] = true;
		}
		
		return arry;
		
		
		
	}
	

	/**
	 * public void arrayToHeap()
	 *
	 * Insert the array to the heap. Delete previous elements in the heap.
	 * 
	 * O(array.length()) amortized
	 */
	public void arrayToHeap(int[] array) {
		this.becomeEmpty();
		for (int i:array){
			this.insert(i);
		}
	}
	
	/**
	 * public boolean isValid()
	 *
	 * Returns true if and only if the heap is valid.
	 * 
	 * O(n)
	 */
	public boolean isValid() {
		if (empty()==true) { //an empty heap is valid
			return true;
		}

		HeapNode thisTree = this.firstNode;
		int count = 0;
		HeapNode temp = this.firstNode;
		while (temp != null) {
			count++;
			temp = temp.getnextSibling();
		}
		//heapDegrees is an array which consist the degrees of all trees
		int[] heapDegrees = new int[count]; 
		int index = 0;
		int degree;
		do {
			degree = countChildren(thisTree);
			heapDegrees[index] = degree;
			index++;
			if (!checkTree(thisTree, degree)) {
				return false;
			}
			thisTree = thisTree.getnextSibling();
		} while (thisTree != null);
		
		
		Arrays.sort(heapDegrees);
		//checking that each degree shows once
		for(int i=0; i<heapDegrees.length-1; i++)
			if(heapDegrees[i] == heapDegrees[i+1])
				return false;
		
		return true; //everything is valid
	}
	
	
	/**public void becomeEmpty()
	 * precondition: none
	 * The method delete all the elements from the heap by updating the relavent fields.
	 * 
	 * O(1)
	 */
	
	public void becomeEmpty() {
		this.firstNode = null;
		this.size = 0;
		this.minNode = null;
	}

	/**public HeapNode getfirstNode()
	 * returns the firstNode.
	 *
	 * precondition: none
	 * 
	 * O(1)
	 */
	public HeapNode getfirstNode() {
		return this.firstNode;
	}


	
	/**private HeapNode link(HeapNode hn1, HeapNode hn2)
	 * the function recursively links HeapNode hn1 and HeapNode hn2
	 * 
	 * O(1)
	 */
	private HeapNode link(HeapNode hn1, HeapNode hn2) {
		
		this.numberOfLinks++;
		if (hn1.getKey()>hn2.getKey()){
			return link(hn2, hn1);
		}
		
		hn2.setParent(hn1);
		hn2.setnextSibling(hn1.getmaxDegreeChild());
		hn1.setmaxDegreeChild(hn2);
		hn1.setDegree(1 + hn1.getDegree());
		return hn1;
	}
	


	/**
	 * Return a new heap which contains the sub trees from this Heap and heap2 
	 * the result is in sorted order of degree.
	 * 
	 * heap2 is the heap that will be united.
	 * precondition: heap2 != null
	 * 
	 * O(logn)
	 */
	private HeapNode merge(BinomialHeap heap2) {
		// If one of the heaps is empty, return the another one
		if (this.firstNode == null)
			return heap2.getfirstNode();
		
		else if (heap2.getfirstNode() == null)
			return this.firstNode;

		HeapNode res;
		HeapNode temp;
		HeapNode h1Temp = this.firstNode;
		HeapNode h2Temp = heap2.getfirstNode();

		// Initializing res and temp to point the minimal degree tree.
		if (this.firstNode.getDegree() <= heap2.getfirstNode().getDegree()) {
			temp = res = this.firstNode;
			h1Temp = h1Temp.getnextSibling();
		} 
		else // mirror case
		{
			temp = res = heap2.getfirstNode();
			h2Temp = h2Temp.getnextSibling();
		}

		while ((h1Temp != null) && (h2Temp != null)) {
			if (h1Temp.getDegree() <= h2Temp.getDegree()) {
				temp.setnextSibling(h1Temp);
				h1Temp = h1Temp.getnextSibling();
			} else {
				temp.setnextSibling(h2Temp);
				h2Temp = h2Temp.getnextSibling();
			}
			temp = temp.getnextSibling();
		}

		// merging the other heap's remaining trees. 
		if (h1Temp != null) {
			temp.setnextSibling(h1Temp);
		} else {
			temp.setnextSibling(h2Temp);
		}

		return res;
	}


	/** private boolean checkTree(HeapNode thisTree, int degree) {
	 *
	 * precondition: curTree!=null
	 * precondition: degree = number of maxDegreeChildren of thisTree
	 * returns true if thisTree is a binomial tree with degree = degree, 
	 * else, returns false
	 * 
	 * O(n)
	 */
	private boolean checkTree(HeapNode thisTree, int degree) {
		if (degree == 0) {
			return true;
		}
		if (thisTree.getmaxDegreeChild() == null) {
			return false;
		}

		HeapNode child = thisTree.getmaxDegreeChild();
		int[] childresTest = new int[degree];
		int tempDegree;

		do {
			if (child.getKey() < thisTree.getKey()) {
				// the heap is not sorted
				return false;
			}
			tempDegree = countChildren(child);
			if (!checkTree(child, tempDegree)) {
				// child isn't valid
				return false;
			}
			if (tempDegree >= degree) {
				// degree of child or degree of parent is not correct.
				return false;
			}
			if (childresTest[tempDegree] == 1) {
				// found two children with same degree
				return false;
			}
			childresTest[tempDegree] = 1;
			child = child.getnextSibling();
		} 
		while (child != null);

		for (int num : childresTest) {
			// checking maxDegreeChild degrees are between :( 0,1...,degree-1 )
			if (num == 0) {
				return false;
			}
		}

		return true; // everything is valid
	}

	/**private int countChildren(HeapNode node)
	 * 
	 * precondition: node != null
	 * returns the number of maxDegreeChild the node has
	 * 
	 * O(logn)
	 */
	private int countChildren(HeapNode node) {
		int count = 0;
		HeapNode n = node.getmaxDegreeChild();
		if (n == null) return 0;
		
		do{
			count++;
			n = n.getnextSibling();
		}
		while (n != null);

		return count;
	}

	/**
	 * public class HeapNode
	 * represents an item in the binomical heap
	 * 
	 */
	public class HeapNode {
		private int key;
		private int degree; // the rank (number of maxDegreeChildren the HeapNode has)
		private HeapNode maxDegreeChild; // a pointer to the maxDegreeChild with highest degree. 
		private HeapNode nextSibling;// a pointer to the sibling with the highest degree after this one)
		private HeapNode parent;

		/**
		 * Constructor
		 * 
		 * O(1)
		 */
		public HeapNode(int k) {
			
			this.key=k;
			this.degree=0;
			this.maxDegreeChild=null;
			this.nextSibling=null;
			this.parent=null;
		}

		/**
		 * getters & setters for all the fields.
		 * 
		 * each method is  O(1)
		 */
		public int getKey() {
			return key;
		}

		public void setKey(int key) {
			this.key = key;
		}

		public int getDegree() {
			return degree;
		}

		public void setDegree(int degree) {
			this.degree = degree;
		}

		public HeapNode getmaxDegreeChild() {
			return maxDegreeChild;
		}

		public void setmaxDegreeChild(HeapNode maxDegreeChild) {
			this.maxDegreeChild = maxDegreeChild;
		}

		public HeapNode getnextSibling() {
			return nextSibling;
		}

		public void setnextSibling(HeapNode nextSibling) {
			this.nextSibling = nextSibling;
		}

		public HeapNode getParent() {
			return parent;
		}

		public void setParent(HeapNode parent) {
			this.parent = parent;
		}
	}
}
