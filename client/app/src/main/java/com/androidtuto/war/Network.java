package com.androidtuto.war;

import com.androidtuto.packet.Packet_Command;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * Created by 초록 on 2015-10-05.
 */
public enum Network {
    INSTANCE;

    public static final int BUF_SIZE = 256;

    //final String SERVER = "192.168.3.71";
    final String SERVER = "54.186.195.189";

    final int PORT = 13431;
    final int TIMEOUT = 10000;

    Socket m_socket;
    private OutputStream m_outstream = null;
    private InputStream m_instream;
    private BufferedReader m_reader;
    private BufferedWriter m_writer;

    NetworkListener m_listener;

    int m_id;

    boolean m_bConnected;

    public boolean init(){

        try {
            m_socket = new Socket(SERVER, PORT);
            m_socket.setTcpNoDelay(true);
            //m_socket.setSoTimeout(TIMEOUT);

            //m_writer = new BufferedWriter(new OutputStreamWriter(m_socket.getOutputStream(), "UTF-8"));
            m_outstream = m_socket.getOutputStream();
            m_instream = m_socket.getInputStream();
            m_writer = new BufferedWriter(new OutputStreamWriter(m_socket.getOutputStream()));
            m_reader = new BufferedReader(new InputStreamReader(m_socket.getInputStream()));

            m_listener = new NetworkListener(m_socket.getInputStream());
            m_listener.start();

            m_id = -1;

            m_bConnected = true;
            return true;

        } catch (IOException ex) {
            //System.err.println(ex);
            m_bConnected = false;
            return false;
        } finally { // dispose
            /*if (m_socket != null) {
                try {
                    m_socket.close();
                } catch (IOException ex) {
                    // ignore
                }
            }*/
        }
    }

    public int getID(){return m_id;}
    public void setID(int id){m_id = id;}

    public boolean isConnected(){return m_bConnected;}

    void write(byte [] b, int size)
    {
        if(m_outstream == null)
            return;

        /*String s = new String(b);
        PrintWriter out = new PrintWriter(m_writer, true);
        out.println(s);*/

        try {
            m_outstream.write(b, 0, size);
            m_outstream.flush();
        }
        catch (IOException ex) {
            //System.err.println(ex);
        }
    }

    void write(Packet_Command p)
    {
        if(m_bConnected == false)
            return;

        byte[] b = new byte[BUF_SIZE];
        p.GetBytes(b);
        Network.INSTANCE.write(b, p.place);
    }

    void destroy()
    {
        /*if(m_listener != null && m_listener.isAlive())
            m_listener.interrupt();
        m_listener.setKill();
        m_listener = null;*/
    }

}
