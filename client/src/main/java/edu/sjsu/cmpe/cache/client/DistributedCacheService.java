package edu.sjsu.cmpe.cache.client;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

/**
 * Distributed cache service
 * 
 */
public class DistributedCacheService implements CacheServiceInterface {
	private String cacheServerUrl;
	private static final SortedMap<Integer, String> circle = new TreeMap<Integer, String>();
	private static HashFunction hashFunction = Hashing.md5();

	public DistributedCacheService(String serverUrl) {
		this.cacheServerUrl = serverUrl;
	}

	public DistributedCacheService(List<String> nodes) {
		for (int i = 0; i < nodes.size(); i++) {
			add(nodes.get(i), i);
		}
	}

	public static void add(String server, int i) {
		HashCode hashCode = hashFunction.hashLong(i);
		circle.put(hashCode.asInt(), server);
	}

	/**
	 * @see edu.sjsu.cmpe.cache.client.CacheServiceInterface#get(long)
	 */
	@Override
	public String get(long key) {
		HttpResponse<JsonNode> response = null;
		try {
			response = Unirest.get(this.cacheServerUrl + "/cache/{key}")
					.header("accept", "application/json")
					.routeParam("key", Long.toString(key)).asJson();
		} catch (UnirestException e) {
			System.err.println(e);
		}
		String value = response.getBody().getObject().getString("value");

		return value;
	}

	/**
	 * @see edu.sjsu.cmpe.cache.client.CacheServiceInterface#put(long,
	 *      java.lang.String)
	 */
	@Override
	public void put(long key, String value) {
		HttpResponse<JsonNode> response = null;
		try {
			response = Unirest
					.put(this.cacheServerUrl + "/cache/{key}/{value}")
					.header("accept", "application/json")
					.routeParam("key", Long.toString(key))
					.routeParam("value", value).asJson();
		} catch (UnirestException e) {
			System.err.println(e);
		}

		if (response.getCode() != 200) {
			System.out.println("Failed to add to the cache.");
		}
	}

	@Override
	public String getServer(Object key) {
		String server = null;
		if (!circle.isEmpty()) {
			int hash = hashFunction.hashLong((Integer) key).asInt();
			if (!circle.containsKey(hash)) {
				SortedMap<Integer, String> tailMap = circle.tailMap(hash);
				hash = tailMap.isEmpty() ? circle.firstKey() : tailMap
						.firstKey();
			}
			server = circle.get(hash);
		}
		return server;
	}

	@Override
	public SortedMap<Integer, String> getCircle() {
		return circle;
	}
}
