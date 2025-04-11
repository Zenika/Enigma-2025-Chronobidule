package com.zenika.enigma.chronobidule.central;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean InetAddress getLocalHostLANAddress() throws UnknownHostException, SocketException {
		try(final DatagramSocket socket = new DatagramSocket(10002)){
			  socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
			  return socket.getLocalAddress();
		}
	}
}
