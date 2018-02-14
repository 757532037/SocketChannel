package com.channel;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Date;

public class TCPProtocolImpl implements TCPProtocol {
    private int bufferSize;

    public TCPProtocolImpl(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public void handleAccept(SelectionKey key) throws IOException {
        // ���ش����˼���ͨ�������ܿͻ��˽������ӵ����󣬲����� SocketChannel ����  
        SocketChannel clientChannel = ((ServerSocketChannel) key.channel()).accept();
        // ������ʽ  
        clientChannel.configureBlocking(false);
        // ע�ᵽselector  
        clientChannel.register(key.selector(), SelectionKey.OP_READ, ByteBuffer.allocate(bufferSize));
    }

    public void handleRead(SelectionKey key) throws IOException {
        // �����ͻ���ͨ�ŵ��ŵ�  
        SocketChannel clientChannel = (SocketChannel) key.channel();
        // �õ�����ջ�����  
        ByteBuffer buffer = (ByteBuffer) key.attachment();
        buffer.clear();
        // ��ȡ��Ϣ��ö�ȡ���ֽ���  
        long bytesRead = clientChannel.read(buffer);
        if (bytesRead == -1) {
            // û�ж�ȡ�����ݵ����  
            clientChannel.close();
        } else {
            // ��������׼��Ϊ���ݴ���״̬  
            buffer.flip();
            // ���ֽ�ת��ΪΪUTF-8���ַ���  
            String receivedString = Charset.forName("UTF-8").newDecoder().decode(buffer).toString();
            // ����̨��ӡ����  
            System.out.println("���յ�����" + clientChannel.socket().getRemoteSocketAddress() + "����Ϣ:" + receivedString);
            // ׼�����͵��ı�  
            String sendString = "���,�ͻ���. @" + new Date().toString() + "���Ѿ��յ������Ϣ" + receivedString;
            buffer = ByteBuffer.wrap(sendString.getBytes("UTF-8"));
            clientChannel.write(buffer);
            // ����Ϊ��һ�ζ�ȡ����д����׼��  
            key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
        }
    }

    public void handleWrite(SelectionKey key) throws IOException {
        // do nothing  
    }
}
