# Path to the jar
JAR="myApp.jar"

# Paths to dataset folders
KB_DIR="data/kb"
RULE_DIR="data/rules"


# Path to output folder

OUTPUT_FILE="data/results/results.csv"



# Check if the CSV file exists
if [ ! -f "$OUTPUT_FILE" ]; then
  # If not, create it and write the header line
  echo "iteration,timestamp,kbSize,ruleSize,elapsedTimeInMillis,memoryUsedInKB" > "$OUTPUT_FILE"
fi



echo
echo "=========================================="
echo "=== Running all rule files with kb-604000.ttl"
echo "=========================================="

# Clean cache (requires root)
echo "Cleaning cache..."
sync
sudo sh -c 'echo 3 > /proc/sys/vm/drop_caches'

echo "Cache cleared."


java  -XX:+UseG1GC  -jar "$JAR" "$KB_DIR/kb-604000.ttl" "$RULE_DIR/rules-5.ttl" 1 >> "$OUTPUT_FILE"

# Clean cache (requires root)
echo "Cleaning cache..."
sync
sudo sh -c 'echo 3 > /proc/sys/vm/drop_caches'

echo "Cache cleared."


java   -XX:+UseG1GC   -jar "$JAR" "$KB_DIR/kb-604000.ttl" "$RULE_DIR/rules-5.ttl" 2 >> "$OUTPUT_FILE"

# Clean cache (requires root)
echo "Cleaning cache..."
sync
sudo sh -c 'echo 3 > /proc/sys/vm/drop_caches'

echo "Cache cleared."


java    -XX:+UseG1GC   -jar "$JAR" "$KB_DIR/kb-604000.ttl" "$RULE_DIR/rules-5.ttl" 3 >> "$OUTPUT_FILE"

# Clean cache (requires root)
echo "Cleaning cache..."
sync
sudo sh -c 'echo 3 > /proc/sys/vm/drop_caches'

echo "Cache cleared."


java      -XX:+UseG1GC  -jar "$JAR" "$KB_DIR/kb-604000.ttl" "$RULE_DIR/rules-5.ttl" 4 >> "$OUTPUT_FILE"

# Clean cache (requires root)
echo "Cleaning cache..."
sync
sudo sh -c 'echo 3 > /proc/sys/vm/drop_caches'

echo "Cache cleared."



java      -XX:+UseG1GC   -jar "$JAR" "$KB_DIR/kb-604000.ttl" "$RULE_DIR/rules-5.ttl" 5 >> "$OUTPUT_FILE"

# Clean cache (requires root)
echo "Cleaning cache..."
sync
sudo sh -c 'echo 3 > /proc/sys/vm/drop_caches'

echo "Cache cleared."



java      -XX:+UseG1GC   -jar "$JAR" "$KB_DIR/kb-604000.ttl" "$RULE_DIR/rules-5.ttl" 6 >> "$OUTPUT_FILE"

# Clean cache (requires root)
echo "Cleaning cache..."
sync
sudo sh -c 'echo 3 > /proc/sys/vm/drop_caches'

echo "Cache cleared."



java     -XX:+UseG1GC   -jar "$JAR" "$KB_DIR/kb-604000.ttl" "$RULE_DIR/rules-5.ttl" 7 >> "$OUTPUT_FILE"


# Clean cache (requires root)
echo "Cleaning cache..."
sync
sudo sh -c 'echo 3 > /proc/sys/vm/drop_caches'

echo "Cache cleared."


java      -XX:+UseG1GC   -jar "$JAR" "$KB_DIR/kb-604000.ttl" "$RULE_DIR/rules-5.ttl" 8 >> "$OUTPUT_FILE"

# Clean cache (requires root)
echo "Cleaning cache..."
sync
sudo sh -c 'echo 3 > /proc/sys/vm/drop_caches'

echo "Cache cleared."



java      -XX:+UseG1GC   -jar "$JAR" "$KB_DIR/kb-604000.ttl" "$RULE_DIR/rules-5.ttl" 9 >> "$OUTPUT_FILE"

# Clean cache (requires root)
echo "Cleaning cache..."
sync
sudo sh -c 'echo 3 > /proc/sys/vm/drop_caches'

echo "Cache cleared."



java       -XX:+UseG1GC   -jar "$JAR" "$KB_DIR/kb-604000.ttl" "$RULE_DIR/rules-5.ttl" 10 >> "$OUTPUT_FILE"


# Clean cache (requires root)
echo "Cleaning cache..."
sync
sudo sh -c 'echo 3 > /proc/sys/vm/drop_caches'

echo "Cache cleared."


java      -XX:+UseG1GC   -jar "$JAR" "$KB_DIR/kb-604000.ttl" "$RULE_DIR/rules-9.ttl" 1 >> "$OUTPUT_FILE"


# Clean cache (requires root)
echo "Cleaning cache..."
sync
sudo sh -c 'echo 3 > /proc/sys/vm/drop_caches'

echo "Cache cleared."


java      -XX:+UseG1GC   -jar "$JAR" "$KB_DIR/kb-604000.ttl" "$RULE_DIR/rules-9.ttl" 2 >> "$OUTPUT_FILE"

# Clean cache (requires root)
echo "Cleaning cache..."
sync
sudo sh -c 'echo 3 > /proc/sys/vm/drop_caches'

echo "Cache cleared."



java      -XX:+UseG1GC   -jar "$JAR" "$KB_DIR/kb-604000.ttl" "$RULE_DIR/rules-9.ttl" 3 >> "$OUTPUT_FILE"

# Clean cache (requires root)
echo "Cleaning cache..."
sync
sudo sh -c 'echo 3 > /proc/sys/vm/drop_caches'

echo "Cache cleared."



java       -XX:+UseG1GC   -jar "$JAR" "$KB_DIR/kb-604000.ttl" "$RULE_DIR/rules-9.ttl" 4 >> "$OUTPUT_FILE"

# Clean cache (requires root)
echo "Cleaning cache..."
sync
sudo sh -c 'echo 3 > /proc/sys/vm/drop_caches'

echo "Cache cleared."



java       -XX:+UseG1GC   -jar "$JAR" "$KB_DIR/kb-604000.ttl" "$RULE_DIR/rules-9.ttl" 5 >> "$OUTPUT_FILE"


# Clean cache (requires root)
echo "Cleaning cache..."
sync
sudo sh -c 'echo 3 > /proc/sys/vm/drop_caches'

echo "Cache cleared."


java       -XX:+UseG1GC   -jar "$JAR" "$KB_DIR/kb-604000.ttl" "$RULE_DIR/rules-9.ttl" 6 >> "$OUTPUT_FILE"


# Clean cache (requires root)
echo "Cleaning cache..."
sync
sudo sh -c 'echo 3 > /proc/sys/vm/drop_caches'

echo "Cache cleared."


java       -XX:+UseG1GC   -jar "$JAR" "$KB_DIR/kb-604000.ttl" "$RULE_DIR/rules-9.ttl" 7 >> "$OUTPUT_FILE"

# Clean cache (requires root)
echo "Cleaning cache..."
sync
sudo sh -c 'echo 3 > /proc/sys/vm/drop_caches'

echo "Cache cleared."



java      -XX:+UseG1GC   -jar "$JAR" "$KB_DIR/kb-604000.ttl" "$RULE_DIR/rules-9.ttl" 8 >> "$OUTPUT_FILE"


# Clean cache (requires root)
echo "Cleaning cache..."
sync
sudo sh -c 'echo 3 > /proc/sys/vm/drop_caches'

echo "Cache cleared."


java      -XX:+UseG1GC   -jar "$JAR" "$KB_DIR/kb-604000.ttl" "$RULE_DIR/rules-9.ttl" 9 >> "$OUTPUT_FILE"


# Clean cache (requires root)
echo "Cleaning cache..."
sync
sudo sh -c 'echo 3 > /proc/sys/vm/drop_caches'

echo "Cache cleared."


java      -XX:+UseG1GC   -jar "$JAR" "$KB_DIR/kb-604000.ttl" "$RULE_DIR/rules-9.ttl" 10 >> "$OUTPUT_FILE"


# Clean cache (requires root)
echo "Cleaning cache..."
sync
sudo sh -c 'echo 3 > /proc/sys/vm/drop_caches'

echo "Cache cleared."



java      -XX:+UseG1GC   -jar "$JAR" "$KB_DIR/kb-604000.ttl" "$RULE_DIR/rules-13.ttl" 1 >> "$OUTPUT_FILE"


# Clean cache (requires root)
echo "Cleaning cache..."
sync
sudo sh -c 'echo 3 > /proc/sys/vm/drop_caches'

echo "Cache cleared."


java      -XX:+UseG1GC   -jar "$JAR" "$KB_DIR/kb-604000.ttl" "$RULE_DIR/rules-13.ttl" 2 >> "$OUTPUT_FILE"

# Clean cache (requires root)
echo "Cleaning cache..."
sync
sudo sh -c 'echo 3 > /proc/sys/vm/drop_caches'

echo "Cache cleared."



java      -XX:+UseG1GC   -jar "$JAR" "$KB_DIR/kb-604000.ttl" "$RULE_DIR/rules-13.ttl" 3 >> "$OUTPUT_FILE"


# Clean cache (requires root)
echo "Cleaning cache..."
sync
sudo sh -c 'echo 3 > /proc/sys/vm/drop_caches'

echo "Cache cleared."


java      -XX:+UseG1GC   -jar "$JAR" "$KB_DIR/kb-604000.ttl" "$RULE_DIR/rules-13.ttl" 4 >> "$OUTPUT_FILE"


# Clean cache (requires root)
echo "Cleaning cache..."
sync
sudo sh -c 'echo 3 > /proc/sys/vm/drop_caches'

echo "Cache cleared."


java      -XX:+UseG1GC   -jar "$JAR" "$KB_DIR/kb-604000.ttl" "$RULE_DIR/rules-13.ttl" 5 >> "$OUTPUT_FILE"

# Clean cache (requires root)
echo "Cleaning cache..."
sync
sudo sh -c 'echo 3 > /proc/sys/vm/drop_caches'

echo "Cache cleared."



java      -XX:+UseG1GC   -jar "$JAR" "$KB_DIR/kb-604000.ttl" "$RULE_DIR/rules-13.ttl" 6 >> "$OUTPUT_FILE"

# Clean cache (requires root)
echo "Cleaning cache..."
sync
sudo sh -c 'echo 3 > /proc/sys/vm/drop_caches'

echo "Cache cleared."



java       -XX:+UseG1GC   -jar "$JAR" "$KB_DIR/kb-604000.ttl" "$RULE_DIR/rules-13.ttl" 7 >> "$OUTPUT_FILE"


# Clean cache (requires root)
echo "Cleaning cache..."
sync
sudo sh -c 'echo 3 > /proc/sys/vm/drop_caches'

echo "Cache cleared."


java      -XX:+UseG1GC   -jar "$JAR" "$KB_DIR/kb-604000.ttl" "$RULE_DIR/rules-13.ttl" 8 >> "$OUTPUT_FILE"


# Clean cache (requires root)
echo "Cleaning cache..."
sync
sudo sh -c 'echo 3 > /proc/sys/vm/drop_caches'

echo "Cache cleared."


java       -XX:+UseG1GC   -jar "$JAR" "$KB_DIR/kb-604000.ttl" "$RULE_DIR/rules-13.ttl" 9 >> "$OUTPUT_FILE"

# Clean cache (requires root)
echo "Cleaning cache..."
sync
sudo sh -c 'echo 3 > /proc/sys/vm/drop_caches'

echo "Cache cleared."



java       -XX:+UseG1GC   -jar "$JAR" "$KB_DIR/kb-604000.ttl" "$RULE_DIR/rules-13.ttl" 10 >> "$OUTPUT_FILE"



# Clean cache (requires root)
echo "Cleaning cache..."
sync
sudo sh -c 'echo 3 > /proc/sys/vm/drop_caches'

echo "Cache cleared."




java      -XX:+UseG1GC   -jar "$JAR" "$KB_DIR/kb-604000.ttl" "$RULE_DIR/rules-17.ttl" 1 >> "$OUTPUT_FILE"


# Clean cache (requires root)
echo "Cleaning cache..."
sync
sudo sh -c 'echo 3 > /proc/sys/vm/drop_caches'

echo "Cache cleared."


java      -XX:+UseG1GC   -jar "$JAR" "$KB_DIR/kb-604000.ttl" "$RULE_DIR/rules-17.ttl" 2 >> "$OUTPUT_FILE"

# Clean cache (requires root)
echo "Cleaning cache..."
sync
sudo sh -c 'echo 3 > /proc/sys/vm/drop_caches'

echo "Cache cleared."



java      -XX:+UseG1GC   -jar "$JAR" "$KB_DIR/kb-604000.ttl" "$RULE_DIR/rules-17.ttl" 3 >> "$OUTPUT_FILE"


# Clean cache (requires root)
echo "Cleaning cache..."
sync
sudo sh -c 'echo 3 > /proc/sys/vm/drop_caches'

echo "Cache cleared."


java      -XX:+UseG1GC   -jar "$JAR" "$KB_DIR/kb-604000.ttl" "$RULE_DIR/rules-17.ttl" 4 >> "$OUTPUT_FILE"


# Clean cache (requires root)
echo "Cleaning cache..."
sync
sudo sh -c 'echo 3 > /proc/sys/vm/drop_caches'

echo "Cache cleared."


java      -XX:+UseG1GC   -jar "$JAR" "$KB_DIR/kb-604000.ttl" "$RULE_DIR/rules-17.ttl" 5 >> "$OUTPUT_FILE"

# Clean cache (requires root)
echo "Cleaning cache..."
sync
sudo sh -c 'echo 3 > /proc/sys/vm/drop_caches'

echo "Cache cleared."



java      -XX:+UseG1GC   -jar "$JAR" "$KB_DIR/kb-604000.ttl" "$RULE_DIR/rules-17.ttl" 6 >> "$OUTPUT_FILE"

# Clean cache (requires root)
echo "Cleaning cache..."
sync
sudo sh -c 'echo 3 > /proc/sys/vm/drop_caches'

echo "Cache cleared."



java       -XX:+UseG1GC   -jar "$JAR" "$KB_DIR/kb-604000.ttl" "$RULE_DIR/rules-17.ttl" 7 >> "$OUTPUT_FILE"


# Clean cache (requires root)
echo "Cleaning cache..."
sync
sudo sh -c 'echo 3 > /proc/sys/vm/drop_caches'

echo "Cache cleared."


java      -XX:+UseG1GC   -jar "$JAR" "$KB_DIR/kb-604000.ttl" "$RULE_DIR/rules-17.ttl" 8 >> "$OUTPUT_FILE"


# Clean cache (requires root)
echo "Cleaning cache..."
sync
sudo sh -c 'echo 3 > /proc/sys/vm/drop_caches'

echo "Cache cleared."


java       -XX:+UseG1GC   -jar "$JAR" "$KB_DIR/kb-604000.ttl" "$RULE_DIR/rules-17.ttl" 9 >> "$OUTPUT_FILE"

# Clean cache (requires root)
echo "Cleaning cache..."
sync
sudo sh -c 'echo 3 > /proc/sys/vm/drop_caches'

echo "Cache cleared."



java       -XX:+UseG1GC   -jar "$JAR" "$KB_DIR/kb-604000.ttl" "$RULE_DIR/rules-17.ttl" 10 >> "$OUTPUT_FILE"



# Clean cache (requires root)
echo "Cleaning cache..."
sync
sudo sh -c 'echo 3 > /proc/sys/vm/drop_caches'

echo "Cache cleared."





java      -XX:+UseG1GC   -jar "$JAR" "$KB_DIR/kb-604000.ttl" "$RULE_DIR/rules-21.ttl" 1 >> "$OUTPUT_FILE"


# Clean cache (requires root)
echo "Cleaning cache..."
sync
sudo sh -c 'echo 3 > /proc/sys/vm/drop_caches'

echo "Cache cleared."


java      -XX:+UseG1GC   -jar "$JAR" "$KB_DIR/kb-604000.ttl" "$RULE_DIR/rules-21.ttl" 2 >> "$OUTPUT_FILE"

# Clean cache (requires root)
echo "Cleaning cache..."
sync
sudo sh -c 'echo 3 > /proc/sys/vm/drop_caches'

echo "Cache cleared."



java      -XX:+UseG1GC   -jar "$JAR" "$KB_DIR/kb-604000.ttl" "$RULE_DIR/rules-21.ttl" 3 >> "$OUTPUT_FILE"


# Clean cache (requires root)
echo "Cleaning cache..."
sync
sudo sh -c 'echo 3 > /proc/sys/vm/drop_caches'

echo "Cache cleared."


java      -XX:+UseG1GC   -jar "$JAR" "$KB_DIR/kb-604000.ttl" "$RULE_DIR/rules-21.ttl" 4 >> "$OUTPUT_FILE"


# Clean cache (requires root)
echo "Cleaning cache..."
sync
sudo sh -c 'echo 3 > /proc/sys/vm/drop_caches'

echo "Cache cleared."


java      -XX:+UseG1GC   -jar "$JAR" "$KB_DIR/kb-604000.ttl" "$RULE_DIR/rules-21.ttl" 5 >> "$OUTPUT_FILE"

# Clean cache (requires root)
echo "Cleaning cache..."
sync
sudo sh -c 'echo 3 > /proc/sys/vm/drop_caches'

echo "Cache cleared."



java      -XX:+UseG1GC   -jar "$JAR" "$KB_DIR/kb-604000.ttl" "$RULE_DIR/rules-21.ttl" 6 >> "$OUTPUT_FILE"

# Clean cache (requires root)
echo "Cleaning cache..."
sync
sudo sh -c 'echo 3 > /proc/sys/vm/drop_caches'

echo "Cache cleared."



java       -XX:+UseG1GC   -jar "$JAR" "$KB_DIR/kb-604000.ttl" "$RULE_DIR/rules-21.ttl" 7 >> "$OUTPUT_FILE"


# Clean cache (requires root)
echo "Cleaning cache..."
sync
sudo sh -c 'echo 3 > /proc/sys/vm/drop_caches'

echo "Cache cleared."


java   -XX:+UseG1GC   -jar "$JAR" "$KB_DIR/kb-604000.ttl" "$RULE_DIR/rules-21.ttl" 8 >> "$OUTPUT_FILE"


# Clean cache (requires root)
echo "Cleaning cache..."
sync
sudo sh -c 'echo 3 > /proc/sys/vm/drop_caches'

echo "Cache cleared."


java    -XX:+UseG1GC   -jar "$JAR" "$KB_DIR/kb-604000.ttl" "$RULE_DIR/rules-21.ttl" 9 >> "$OUTPUT_FILE"

# Clean cache (requires root)
echo "Cleaning cache..."
sync
sudo sh -c 'echo 3 > /proc/sys/vm/drop_caches'

echo "Cache cleared."



java   -XX:+UseG1GC   -jar "$JAR" "$KB_DIR/kb-604000.ttl" "$RULE_DIR/rules-21.ttl" 10 >> "$OUTPUT_FILE"



# Clean cache (requires root)
echo "Cleaning cache..."
sync
sudo sh -c 'echo 3 > /proc/sys/vm/drop_caches'

echo "Cache cleared."





