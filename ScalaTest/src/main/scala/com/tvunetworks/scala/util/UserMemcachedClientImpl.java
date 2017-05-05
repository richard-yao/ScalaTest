package com.tvunetworks.scala.util;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.spy.memcached.AddrUtil;
import net.spy.memcached.DefaultConnectionFactory;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.MemcachedNode;
import net.spy.memcached.NodeLocator;

import org.apache.commons.lang.StringUtils;


public class UserMemcachedClientImpl {
	
	private String hosts = null;
	private long timeout = 7000;
	private MemcachedClient mc = null;
	private static UserMemcachedClientImpl instance = null;
	
	private UserMemcachedClientImpl() throws IOException {
	}
	
	private UserMemcachedClientImpl(String hosts, long timeout) throws IOException {
		this.hosts = hosts;
		this.timeout = timeout;
	}

	private synchronized MemcachedClient getMemcacedClient() throws IOException {
		if (this.mc == null) {
			this.mc = new MemcachedClient(new DefaultConnectionFactory() {
				@Override
				public long getOperationTimeout() {
					return timeout;
				}

			}, AddrUtil.getAddresses(hosts));
		}
		return this.mc;
	}

	public void setHosts(String hostString) {
		this.hosts = hostString;
	}
	
	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	public static synchronized UserMemcachedClientImpl getInstance(String hosts, long timeout) throws IOException {
		if (instance == null) {
			instance = new UserMemcachedClientImpl(hosts, timeout);
		}
		return instance;
	}

	public void destroy() {
		this.mc.shutdown();
	}

	public void set(String key, Object value, int exp) throws Exception {
		if (StringUtils.isBlank(key)) {
			throw new Exception("key is null.");
		}
		this.getMemcacedClient().set(key, exp, value);
	}

	public Object get(String key) throws Exception {
		if (StringUtils.isBlank(key)) {
			throw new Exception("key is null.");
		}
		Object object = this.getMemcacedClient().get(key);
		return object;
	}

	public Map<String, Object>  getMulti(Collection<String> keys) throws Exception {
		Map<String, Object>  objMap = this.getMemcacedClient().getBulk(keys);
		return objMap;
	}

	public String getServerInfo(String key) {
		try {
			NodeLocator nl = mc.getNodeLocator();
			MemcachedNode md = nl.getPrimary(key);
			SocketAddress sa = md.getSocketAddress();
			return sa.toString();
		} catch (Exception e) {
			return "";
		}
	}

	public Set<String> getServerInfos() {
		Set<String> ss = new HashSet<String>();
		String[] hs = hosts.split(" ");
		for (String h : hs) {
			ss.add("/" + h.trim());
		}
		return ss;
	}

	public boolean allServerIsAvailable() {
		try {
			Set<String> hs = getServerInfos();
			Set<SocketAddress> as = this.getMemcacedClient().getVersions().keySet();
			for (String h : hs) {
				if (!isExistServer(h, as)) {
					return false;
				}
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean isExistServer(String server, Set<SocketAddress> as) {
		for (SocketAddress a : as) {
			if (a.toString().equals(server)) {
				return true;
			}
		}
		return false;
	}

	public void delete(String key) throws Exception {
		if (key != null) {
			getMemcacedClient().delete(key);
		}
	}
	
}


