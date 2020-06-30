package br.com.panico;

import java.io.*;

@SuppressWarnings( "ALL" )
public  class RedOutputStream extends BufferedOutputStream {
    public RedOutputStream(){
        super(null);
    }
    public RedOutputStream(OutputStream out){
        this(out, 8192 );
    }

    public RedOutputStream(OutputStream out, int size) {
        super(out, size);
    }

    public synchronized byte[] toByteArray () {
        byte newbuf[] = new byte[super.count];
        System.arraycopy(super.buf, 0, newbuf, 0, super.count);
        return newbuf;
    }

    public int size() {
        return super.count;
    }

    public String toString() {
        return new String(super.buf, 0, super.count);
    }

    public String toString (String enc) throws UnsupportedEncodingException {
        return new String(super.buf, 0, super.count, enc);
    }
    public int read(byte[] b, int off, int len) throws IOException {
        throw new RuntimeException("Stub!");
    }
}
