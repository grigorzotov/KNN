package knn;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Collectors;


public class KNearestNeighbours {

    public static void main(String[] args) throws IOException {
    	//Input
        Scanner sc = new Scanner(System.in);
        System.out.println("Please enter k: ");
        int k = sc.nextInt();
        sc.close();

        //Extract Data
    	List<Iris> dataSet = new ArrayList<>();
		BufferedReader br = new BufferedReader(new FileReader("src/iris.txt"));
		try {
			String line;
			while((line = br.readLine()) != null) {
			    String[] fields = line.split(",");
			    dataSet.add(new Iris(Double.parseDouble(fields[0]), Double.parseDouble(fields[1]), Double.parseDouble(fields[2]), Double.parseDouble(fields[3]), fields[4]));
				}
			} finally {
		    br.close();
		}
		
		//Get Testing Random Set of 20 individuals
		List<Iris> testSet = extractTestSubset(dataSet);
		
        Map<Iris, String> predictions = new HashMap<>();
     
        //Get Predictions and Accuracy
        for (Iris testInstance : testSet) {
            List<Iris> neighbours = getNeighbours(dataSet, testInstance, k);
            String response = getResponse(neighbours);
            System.out.println("Prediction: " + response + " for: ");
            System.out.println("Iris [sepalLength=" + testInstance.sepalLength + ", sepalWidth=" + testInstance.sepalWidth +
            		", petalLength=" + testInstance.petalLength
                    + ", petalWidth=" + testInstance.petalWidth + ", name=" + testInstance.name + "]");
            System.out.println();
            predictions.put(testInstance, response);
        }

        System.out.println("Accuracy: " + getAccuracy(predictions));
    }

    //Get Testing Random Set of 20 individuals
    private static List<Iris> extractTestSubset(List<Iris> dataSet) {
        List<Iris> testSet = new ArrayList<>();

        while (testSet.size() < 20) {
            Random rand = new Random(System.nanoTime());
            int randomIndex = rand.nextInt(dataSet.size());
            testSet.add(dataSet.get(randomIndex));
            dataSet.remove(randomIndex);
        }

        return testSet;
    }

    // Get Percent of correct predictions for test parameters
    private static double getAccuracy(Map<Iris, String> predictions) {
        int correctCount = 0;
        for (Map.Entry<Iris, String> entry : predictions.entrySet()) {
            if (entry.getValue().equals(entry.getKey().name)) {
                correctCount++;
            }
        }
        return ((correctCount * 1.0) / predictions.size()) * 100.0;
    }

    
    private static String getResponse(List<Iris> neighbours) {
        Map<String, Integer> votes = new HashMap<>();
        for (Iris neighbour : neighbours) {
            Integer currentCount = votes.get(neighbour.name);
            votes.put(neighbour.name, currentCount == null ? 1 : currentCount + 1);
        }

        LinkedHashMap<String, Integer> sorted = votes.entrySet()
                                                     .stream()
                                                     .sorted(Map.Entry.comparingByValue((a, b) -> b - a))
                                                     .collect(Collectors.toMap(Map.Entry::getKey,
                                                                               Map.Entry::getValue,
                                                                               (e1, e2) -> e1,
                                                                               LinkedHashMap::new));
        return sorted.entrySet().iterator().next().getKey();
    }

    private static List<Iris> getNeighbours(List<Iris> trainingSet, Iris testInstance, int k) {
        List<Iris> neighbours = new ArrayList<>();
        Map<Integer, Double> distances = new HashMap<>();
        for (int i = 0; i < trainingSet.size(); i++) {
            distances.put(i, getEucledianDistance(trainingSet.get(i), testInstance));
        }

        Map<Integer, Double> sorted = distances.entrySet()
                                               .stream()
                                               .sorted(Map.Entry.comparingByValue((a, b) -> (int) Math.signum(a - b)))
                                               .collect(Collectors.toMap(Map.Entry::getKey,
                                                                         Map.Entry::getValue,
                                                                         (e1, e2) -> e1,
                                                                         LinkedHashMap::new));

        Iterator<Integer> it = sorted.keySet().iterator();
        int counter = 0;
        while (counter++ < k) {
            Integer index = it.next();
            neighbours.add(trainingSet.get(index));
        }

        return neighbours;
    }

    private static double getEucledianDistance(Iris instance1, Iris instance2) {
        double distance = Math.pow(instance1.petalLength - instance2.petalLength, 2)
                        + Math.pow(instance1.petalWidth - instance2.petalWidth, 2)
                        + Math.pow(instance1.sepalLength - instance2.sepalLength, 2)
                        + Math.pow(instance1.sepalWidth - instance2.sepalWidth, 2);
        return Math.sqrt(distance);
    }



    static class Iris {
        public double sepalLength;
        public double sepalWidth;
        public double petalLength;
        public double petalWidth;
        public String name;

        public Iris(double sepalLength, double sepalWidth, double petalLength, double petalWidth, String name) {
            this.sepalLength = sepalLength;
            this.sepalWidth = sepalWidth;
            this.petalLength = petalLength;
            this.petalWidth = petalWidth;
            this.name = name;
        }

    }
}
