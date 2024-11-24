package simpledb;

import java.io.*;
import java.util.*;

/**
 * HeapFile is an implementation of a DbFile that stores a collection of tuples
 * in no particular order. Tuples are stored on pages, each of which is a fixed
 * size, and the file is simply a collection of those pages. HeapFile works
 * closely with HeapPage. The format of HeapPages is described in the HeapPage
 * constructor.
 * 
 * @see simpledb.HeapPage#HeapPage
 * @author Sam Madden
 */
public class HeapFile implements DbFile {
    public File file;
    public TupleDesc td;

    /**
     * Constructs a heap file backed by the specified file.
     * 
     * @param f
     *            the file that stores the on-disk backing store for this heap
     *            file.
     */
    public HeapFile(File f, TupleDesc td) {
        // some code goes here
        this.file=f;
        this.td=td;
    }

    /**
     * Returns the File backing this HeapFile on disk.
     * 
     * @return the File backing this HeapFile on disk.
     */
    public File getFile() {
        // some code goes here
        return this.file;
    }

    /**
     * Returns an ID uniquely identifying this HeapFile. Implementation note:
     * you will need to generate this tableid somewhere to ensure that each
     * HeapFile has a "unique id," and that you always return the same value for
     * a particular HeapFile. We suggest hashing the absolute file name of the
     * file underlying the heapfile, i.e. f.getAbsoluteFile().hashCode().
     * 
     * @return an ID uniquely identifying this HeapFile.
     */
    public int getId() {
        // some code goes here
        // throw new UnsupportedOperationException("implement this"); // (?) What to do with this exception?
        return file.getAbsoluteFile().hashCode();
    }

    /**
     * Returns the TupleDesc of the table stored in this DbFile.
     * 
     * @return TupleDesc of this DbFile.
     */
    public TupleDesc getTupleDesc() {
        // some code goes here
        // throw new UnsupportedOperationException("implement this"); // (?) What to do with this exception?
        return this.td;
    }

    // see DbFile.java for javadocs
    public Page readPage(PageId pid) {
        // some code goes here
        int pageSize = BufferPool.getPageSize();
        int pageNumber = pid.getPageNumber();
        byte[] pageData = new byte[pageSize];
        RandomAccessFile raf = null;

        try {
            raf = new RandomAccessFile(file, "r");
            long offset = pageNumber * pageSize;
            if (offset >= raf.length()) {
                throw new IllegalArgumentException("Page doesnt exist in this file");
            }
            raf.seek(offset); e
            raf.readFully(pageData);
            return new HeapPage((HeapPageId) pid, pageData);
        } catch (IOException e) {
            throw new IllegalArgumentException("Error reading page", e);
        }
    }

    // see DbFile.java for javadocs
    public void writePage(Page page) throws IOException {
        // some code goes here
        // not necessary for lab1
    }

    /**
     * Returns the number of pages in this HeapFile.
     */
    public int numPages() {
        // some code goes here
        return (int) (file.length()/BufferPool.getPageSize());
    }

    // see DbFile.java for javadocs
    public ArrayList<Page> insertTuple(TransactionId tid, Tuple t)
            throws DbException, IOException, TransactionAbortedException {
        // some code goes here
        return null;
        // not necessary for lab1
    }

    // see DbFile.java for javadocs
    public ArrayList<Page> deleteTuple(TransactionId tid, Tuple t) throws DbException,
            TransactionAbortedException {
        // some code goes here
        return null;
        // not necessary for lab1
    }

    // see DbFile.java for javadocs
    public DbFileIterator iterator(TransactionId tid) {
        // some code goes here
        return new DbFileIterator() {
            private int currentPageIndex = -1; // Index of the current page being processed
            private Iterator<Tuple> currentPageIterator = null; // Iterator for tuples on the current page

            @Override
            public void open() throws DbException, TransactionAbortedException {
                currentPageIndex = 0; // Start with the first page
                currentPageIterator = getPageIterator(currentPageIndex); // Load tuples from the first page
            }

            @Override
            public boolean hasNext() throws DbException, TransactionAbortedException {
                if (currentPageIterator == null) return false; // Iterator hasn't been opened

                // Check if there are more tuples on the current page
                if (currentPageIterator.hasNext()) {
                    return true;
                }

                // If no more tuples on the current page, move to the next page if available
                while (currentPageIndex < numPages() - 1) {
                    currentPageIndex++;
                    currentPageIterator = getPageIterator(currentPageIndex); // Load the next page's tuples
                    if (currentPageIterator.hasNext()) {
                        return true; // Found tuples on the next page
                    }
                }
                return false; // No more tuples in the file
            }

            @Override
            public Tuple next() throws DbException, TransactionAbortedException, NoSuchElementException {
                if (currentPageIterator == null || !currentPageIterator.hasNext()) {
                    throw new NoSuchElementException();
                }
                return currentPageIterator.next(); // Return the next tuple
            }

            @Override
            public void rewind() throws DbException, TransactionAbortedException {
                close(); // Reset the iterator
                open();  // Reopen to start from the beginning
            }

            @Override
            public void close() {
                currentPageIndex = -1; // Reset the page index
                currentPageIterator = null; // Clear the current page iterator
            }

            // Helper method to get an iterator for a specific page
            private Iterator<Tuple> getPageIterator(int pageIndex) throws DbException, TransactionAbortedException {
                PageId pid = new HeapPageId(getId(), pageIndex); // Create a PageId for the page
                HeapPage page = (HeapPage) Database.getBufferPool().getPage(tid, pid, Permissions.READ_ONLY);
                return page.iterator(); // Return the iterator for tuples on the page
            }
        };
    }

}

