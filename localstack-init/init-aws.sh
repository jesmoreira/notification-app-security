#!/bin/bash
echo "=== Creating SQS Queues ==="

awslocal sqs create-queue --queue-name notifications_dlq

awslocal sqs create-queue --queue-name notifications_queue \
  --attributes '{"RedrivePolicy": "{\"deadLetterTargetArn\":\"arn:aws:sqs:us-east-1:000000000000:notifications_dlq\",\"maxReceiveCount\":\"3\"}"}'

echo "=== SQS Queues created! ==="