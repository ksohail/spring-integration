/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.integration.ip.tcp.connection;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

import org.springframework.util.Assert;

/**
 * A client connection factory that creates {@link TcpNetConnection}s.
 * @author Gary Russell
 * @since 2.0
 *
 */
public class TcpNetClientConnectionFactory extends
		AbstractClientConnectionFactory {

	private TcpSocketFactorySupport tcpSocketFactorySupport = new DefaultTcpNetSocketFactorySupport();

	private TcpNetConnectionSupport tcpNetConnectionSupport = new DefaultTcpNetConnectionSupport();

	/**
	 * Creates a TcpNetClientConnectionFactory for connections to the host and port.
	 * @param host the host
	 * @param port the port
	 */
	public TcpNetClientConnectionFactory(String host, int port) {
		super(host, port);
	}

	@Override
	protected TcpConnectionSupport buildNewConnection() throws IOException, SocketException, Exception {
		Socket socket = createSocket(this.getHost(), this.getPort());
		setSocketAttributes(socket);
		TcpConnectionSupport connection = this.tcpNetConnectionSupport.createNewConnection(socket, false, isLookupHost(),
				getApplicationEventPublisher(), getComponentName());
		connection = wrapConnection(connection);
		initializeConnection(connection, socket);
		this.getTaskExecutor().execute(connection);
		this.harvestClosedConnections();
		return connection;
	}

	/**
	 * Set the {@link TcpNetConnectionSupport} to use to create connection objects.
	 * @param connectionSupport the connection support.
	 * @since 5.0
	 */
	public void setTcpNetConnectionSupport(TcpNetConnectionSupport connectionSupport) {
		Assert.notNull(connectionSupport, "'connectionSupport' cannot be null");
		this.tcpNetConnectionSupport = connectionSupport;
	}

	@Override
	public void start() {
		this.setActive(true);
		super.start();
	}

	/**
	 * Create a new {@link Socket}. This default implementation uses the default
	 * {@link javax.net.SocketFactory}. Override to use some other mechanism
	 *
	 * @param host The host.
	 * @param port The port.
	 * @return The Socket
	 * @throws IOException Any IOException.
	 */
	protected Socket createSocket(String host, int port) throws IOException {
		return this.tcpSocketFactorySupport.getSocketFactory().createSocket(host, port);
	}

	protected TcpSocketFactorySupport getTcpSocketFactorySupport() {
		return this.tcpSocketFactorySupport;
	}

	public void setTcpSocketFactorySupport(
			TcpSocketFactorySupport tcpSocketFactorySupport) {
		Assert.notNull(tcpSocketFactorySupport, "TcpSocketFactorySupport may not be null");
		this.tcpSocketFactorySupport = tcpSocketFactorySupport;
	}

}
