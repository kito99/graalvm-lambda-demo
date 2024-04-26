#!/bin/bash

# Array of URLs to send requests to
urls=("https://bchs2cynrg.execute-api.us-east-1.amazonaws.com/hello-world-jvm-snapstert"
"https://bchs2cynrg.execute-api.us-east-1.amazonaws.com/hello-world-jvm"
"https://bchs2cynrg.execute-api.us-east-1.amazonaws.com/hello-world-graal")

# Number of requests to send
num_requests=10

# Loop over each URL
for url in "${urls[@]}"; do
    echo "Sending requests to: $url"

    # Initialize total time
    total_time=0

    # Send requests and calculate total time
    for ((i=1;i<=num_requests;i++)); do
        # Send request and get time in seconds
        time=$(curl -o /dev/null -s -w %{time_total} $url)

        # Convert time to milliseconds
        time=$(echo "$time * 1000" | bc)

        echo "Request $i: $time milliseconds"
        total_time=$(echo $total_time+$time | bc)
    done

    # Calculate average time
    average_time=$(echo "scale=2; $total_time/$num_requests" | bc)

    echo "Average response time for $url: $average_time milliseconds"
done
