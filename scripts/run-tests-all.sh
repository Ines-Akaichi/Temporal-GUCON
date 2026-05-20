#!/bin/sh

# Path to the jar
JAR="gucon.obligations.testHarness-0.0.2-SNAPSHOT.jar"

# Paths to dataset folders
KB_DIR="data/kb"
RULE_DIR_HIGH="data/rules/high"
RULE_DIR_MEDIUM="data/rules/medium"
RULE_DIR_LOW="data/rules/low"

# Path to output folder

OUTPUT_FILE="data/results/results-task2.csv"


# Check if the CSV file exists
if [ ! -f "$OUTPUT_FILE" ]; then
  # If not, create it and write the header line
  echo "iteration,timestamp,kbSize,ruleSize,elapsedTimeInMillis,memoryUsedInKB" > "$OUTPUT_FILE"
fi


echo "=========================================="
echo "=== Running all KB files with HIGH rules-13.ttl"
echo "=========================================="

for kbSize in 500000 909068 1318136 1727204 2136272 2545342 ; do

	file_path="$KB_DIR/kb-$kbSize.ttl"

	for i in 1 2 3 4 5 6 7 8 9 10 ; do

           	 	# Clean cache (requires root)
		echo "Cleaning cache..."
		sync
		sudo sh -c 'echo 3 > /proc/sys/vm/drop_caches'

		echo "Cache cleared."


		java   -jar "$JAR" "$file_path" "$RULE_DIR_HIGH/rules-13.ttl" "$i" >> "$OUTPUT_FILE"

	done

done


echo "=========================================="
echo "=== Running all KB files with MEDIUM rules-10.ttl"
echo "=========================================="

for kbSize in 500000 909068 1318136 1727204 2136272 2545342 ; do

	file_path="$KB_DIR/kb-$kbSize.ttl"

	for i in 1 2 3 4 5 6 7 8 9 10 ; do

           	 	# Clean cache (requires root)
		echo "Cleaning cache..."
		sync
		sudo sh -c 'echo 3 > /proc/sys/vm/drop_caches'

		echo "Cache cleared."


		java   -jar "$JAR" "$file_path" "$RULE_DIR_MEDIUM/rules-10.ttl" "$i" >> "$OUTPUT_FILE"

	done

done



echo "=========================================="
echo "=== Running all KB files with LOW rules-13.ttl"
echo "=========================================="

for kbSize in 500000 909068 1318136 1727204 2136272 2545342 ; do

	file_path="$KB_DIR/kb-$kbSize.ttl"

	for i in 1 2 3 4 5 6 7 8 9 10 ; do

           	 	# Clean cache (requires root)
		echo "Cleaning cache..."
		sync
		sudo sh -c 'echo 3 > /proc/sys/vm/drop_caches'

		echo "Cache cleared."


		java   -jar "$JAR" "$file_path" "$RULE_DIR_LOW/rules-13.ttl" "$i" >> "$OUTPUT_FILE"

	done

done





# Path to output folder

OUTPUT_FILE1="data/results/results-task1.csv"


# Check if the CSV file exists
if [ ! -f "$OUTPUT_FILE1" ]; then
  # If not, create it and write the header line
  echo "iteration,timestamp,kbSize,ruleSize,elapsedTimeInMillis,memoryUsedInKB" > "$OUTPUT_FILE1"
fi


echo "=========================================="
echo "=== Running all HIGH rules files with kb-1318136.ttl "
echo "=========================================="

for ruleSize in 5 9 13 17 21 ; do

	file_path="$RULE_DIR_HIGH/rules-$ruleSize.ttl"

	for i in 1 2 3 4 5 6 7 8 9 10 ; do

           	 	# Clean cache (requires root)
		echo "Cleaning cache..."
		sync
		sudo sh -c 'echo 3 > /proc/sys/vm/drop_caches'

		echo "Cache cleared."


		java   -jar "$JAR" "$KB_DIR/kb-1318136.ttl" "$file_path" "$i" >> "$OUTPUT_FILE1"

	done

done



echo "=========================================="
echo "=== Running all MEDIUM rules files with kb-1318136.ttl "
echo "=========================================="

for ruleSize in 6 8 10 12 14 ; do

	file_path="$RULE_DIR_MEDIUM/rules-$ruleSize.ttl"

	for i in 1 2 3 4 5 6 7 8 9 10 ; do

           	 	# Clean cache (requires root)
		echo "Cleaning cache..."
		sync
		sudo sh -c 'echo 3 > /proc/sys/vm/drop_caches'

		echo "Cache cleared."


		java   -jar "$JAR" "$KB_DIR/kb-1318136.ttl" "$file_path" "$i" >> "$OUTPUT_FILE1"

	done

done



echo "=========================================="
echo "=== Running all LOW rules files with kb-1318136.ttl "
echo "=========================================="

for ruleSize in 5 9 13 17 21 ; do

	file_path="$RULE_DIR_LOW/rules-$ruleSize.ttl"

	for i in 1 2 3 4 5 6 7 8 9 10 ; do

           	 	# Clean cache (requires root)
		echo "Cleaning cache..."
		sync
		sudo sh -c 'echo 3 > /proc/sys/vm/drop_caches'

		echo "Cache cleared."


		java   -jar "$JAR" "$KB_DIR/kb-1318136.ttl" "$file_path" "$i" >> "$OUTPUT_FILE1"

	done

done


