//package hw1.ml;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class ComputeInformationGain {

	HashMap<String, Double> varianceMap;
	HashMap<String, ArrayList<String>> dataMap;
	HashMap<String, Double> gainMap;
	HashMap<String, ArrayList<String>> mapVarianceData;

	public static HashMap<String, ArrayList<String>> mapData(
			ArrayList<ArrayList<String>> data) throws FileNotFoundException {
		HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();

		ArrayList<String> keys = data.get(0); // taking all the keys from the
												// first row of the data

		for (int i = 0; i < keys.size(); i++) {
			for (int j = 1; j < data.size(); j++) {
				if (map.containsKey(keys.get(i))) {
					map.get(keys.get(i)).add(data.get(j).get(i));
				} else {
					ArrayList<String> values = new ArrayList<String>();
					values.add(data.get(j).get(i));
					map.put(keys.get(i), values);
				}
			}
		}
		return map;
	}

	/**
	 * Calculating the log of base 2
	 */
	public static double log2(double n, double base) {
		return Math.log(n) / Math.log(base);
	}

	/**
	 * Calculating entropy on the basis of positives and negative values
	 */
	public static double entropyCompute(double positives, double negatives) {
		double total = positives + negatives;
		double probabilityPositive = positives / total;
		double probabilityNegative = negatives / total;

		if (positives == negatives) {
			return 1;
		} else if (positives == 0 || negatives == 0) {
			return 0;
		} else {
			double entropy = ((-probabilityPositive) * (log2(
					probabilityPositive, 2)))
					+ ((-probabilityNegative) * (log2(probabilityNegative, 2)));
			return entropy;
		}

	}

	public static HashMap<String, ArrayList<ArrayList<String>>> bestAttributeClassification(
			ArrayList<ArrayList<String>> data, String bestAttribute) {
		HashMap<String, ArrayList<ArrayList<String>>> reducedMap = new HashMap<String, ArrayList<ArrayList<String>>>();
		int index = data.get(0).indexOf(bestAttribute);
		// ArrayList<ArrayList<String>> head = data.get(0);
		for (int i = 1; i < data.size(); i++) {
			if (data.get(i).get(index).equalsIgnoreCase("0")) {
				if (reducedMap.containsKey("0")) {
					reducedMap.get("0").add(data.get(i));
				} else {
					ArrayList<ArrayList<String>> reducedData = new ArrayList<ArrayList<String>>();
					reducedData.add(data.get(0));
					reducedData.add(data.get(i));
					reducedMap.put("0", reducedData);
				}

			} else {
				if (reducedMap.containsKey("1")) {
					reducedMap.get("1").add(data.get(i));
				} else {
					ArrayList<ArrayList<String>> reducedData = new ArrayList<ArrayList<String>>();
					reducedData.add(data.get(0));
					reducedData.add(data.get(i));
					reducedMap.put("1", reducedData);
				}
			}
		}

		return reducedMap;
	}

	/**
	 * Compute the information gain
	 */
	public double informationGain(double positiveParent, double negativeParent,
			double positiveLeft, double negativeLeft, double positiveRight,
			double negativeRight) {
		double totalRoot = positiveParent + negativeParent;

		double parentEntropy = entropyCompute(positiveParent, negativeParent);
		double leftEntropy = entropyCompute(positiveLeft, negativeLeft);

		double rightEntropy = entropyCompute(positiveRight, negativeRight);
		double totalLeft = positiveLeft + negativeLeft;
		double totalRight = positiveRight + negativeRight;

		double infoGain = parentEntropy
				- (((totalLeft / totalRoot) * leftEntropy) + ((totalRight / totalRoot) * rightEntropy));

		return infoGain;
	}

	/**
	 * Function to calculate best attribute
	 * 
	 * @param attributes
	 * @param root
	 * @throws FileNotFoundException
	 */

	public String computeBestAttr(ArrayList<ArrayList<String>> data,
			ArrayList<String> attributeList) throws FileNotFoundException {
		String bestAttribute = "";
		dataMap = mapData(data);
		gainMap = new HashMap<String, Double>();
		double classPositive = 0;
		double classNegative = 0;
		for (String value : dataMap.get("Class")) {
			if (value.equalsIgnoreCase("1")) {
				classPositive++;
			} else {
				classNegative++;
			}
		}

		for (String key : attributeList.subList(0, attributeList.size() - 1)) { // the
																				// keys
																				// can
																				// be
																				// replaced
																				// by
																				// the
																				// attribute
																				// list
																				// that
																				// can
																				// be
																				// recursively
																				// fed.
			ArrayList<String> temp = dataMap.get(key);
			double positiveLeft = 0;
			double positiveRight = 0;
			double negativeLeft = 0;
			double negativeRight = 0;
			for (int i = 0; i < temp.size(); i++) { // loop to check the no of
													// positive instances for 0
													// and 1 for each attribute
				if (temp.get(i).equalsIgnoreCase("0")) {
					if (dataMap.get("Class").get(i).equalsIgnoreCase("1")) {
						positiveLeft++;
					} else {
						negativeLeft++;
					}
				} else {
					if (dataMap.get("Class").get(i).equalsIgnoreCase("1")) {
						positiveRight++;
					} else {
						negativeRight++;
					}
				}
			}

			Double gainForEachKey = informationGain(classPositive,
					classNegative, positiveLeft, negativeLeft, positiveRight,
					negativeRight);
			gainMap.put(key, gainForEachKey);
		}

		ArrayList<Double> valueList = new ArrayList<Double>(gainMap.values());
		Collections.sort(valueList);
		Collections.reverse(valueList);
		for (String key : gainMap.keySet()) {
			if (valueList.get(0).equals(gainMap.get(key))) {
				bestAttribute = key;
				break;
			}
		}
		return bestAttribute;
	}

}
