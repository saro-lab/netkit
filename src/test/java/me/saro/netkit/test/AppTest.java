package me.saro.netkit.test;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import me.saro.commons.Lambdas;
import me.saro.netkit.NetkitServer;

public class AppTest {

    public static void main( String[] args ) throws Exception {

        NetkitServer
            .bind(2100)
            .accept()
                .prev(null)
                .next(null)
                .error(null)
            .read(null);

        System.in.read();
    }

    public static void tempSendAll() throws UnknownHostException, IOException {

//        tempSend("Java is a general-purpose computer-programming language that is concurrent, class-based, object-oriented,[15] and specifically designed to have as few implementation dependencies as possible. It is intended to let application developers \"write once, run anywhere\" (WORA),[16] meaning that compiled Java code can run on all platforms that support Java without the need for recompilation.[17] Java applications are typically compiled to bytecode that can run on any Java virtual machine (JVM) regardless of computer architecture. As of 2016, Java is one of the most popular programming languages in use,[18][19][20][21] particularly for client-server web applications, with a reported 9 million developers.[22] Java was originally developed by James Gosling at Sun Microsystems (which has since been acquired by Oracle Corporation) and released in 1995 as a core component of Sun Microsystems' Java platform. The language derives much of its syntax from C and C++, but it has fewer low-level facilities than either of them.\r\nThe original and reference implementation Java compilers, virtual machines, and class libraries were originally released by Sun under proprietary licenses. As of May 2007, in compliance with the specifications of the Java Community Process, Sun relicensed most of its Java technologies under the GNU General Public License. Others have also developed alternative implementations of these Sun technologies, such as the GNU Compiler for Java (bytecode compiler), GNU Classpath (standard libraries), and IcedTea-Web (browser plugin for applets).");
//        tempSend("자바는 썬 마이크로시스템즈의 제임스 고슬링(James Gosling)과 다른 연구원들이 개발한 객체 지향적 프로그래밍 언어이다. 1991년 그린 프로젝트(Green Project)라는 이름으로 시작해 1995년에 발표했다. 처음에는 가전제품 내에 탑재해 동작하는 프로그램을 위해 개발했지만 현재 웹 애플리케이션 개발에 가장 많이 사용하는 언어 가운데 하나이고, 모바일 기기용 소프트웨어 개발에도 널리 사용하고 있다. 현재 버전 10까지 출시했다.");

    }

    public static void tempSend(String abc) {
        new Thread( Lambdas.runtime( () -> { 
            Socket socket = new Socket("127.0.0.1", 2100);
            var a = socket.getOutputStream();
            a.write(abc.getBytes());
            socket.close();
        } )).start();
    }

}
