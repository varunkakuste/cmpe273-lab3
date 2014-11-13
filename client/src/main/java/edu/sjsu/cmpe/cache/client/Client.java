package edu.sjsu.cmpe.cache.client;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

import com.google.common.hash.Hashing;

public class Client {

	public static void main(String[] args) throws Exception {
		System.out.println("Starting Cache Client...\n");

		List<String> nodeList = new ArrayList<String>();
		nodeList.add("http://localhost:3000");
		nodeList.add("http://localhost:3001");
		nodeList.add("http://localhost:3002");

		char[] chars = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j' };
		int bucket;
		String server = null;

		CacheServiceInterface cache = new DistributedCacheService(nodeList);
		CacheServiceInterface cacheSingleServer = null;
		SortedMap<Integer, String> circle = cache.getCircle();

		for (int j = 0; j < 10; j++) {
			bucket = Hashing.consistentHash(Hashing.md5().hashInt(j), circle.size());
			server = cache.getServer(bucket);
			System.out.println("Object added to Server: " + server);
			cacheSingleServer = new DistributedCacheService(server);
			cacheSingleServer.put(j + 1, String.valueOf(chars[j]));
			System.out.println("put(" + (j + 1) + ") => " + String.valueOf(chars[j]));
			String value = cacheSingleServer.get(j + 1);
			System.out.println("get(" + (j + 1) + ") => " + value + "\n");
		}

		System.out.println("Existing Cache Client...");
	}

}
