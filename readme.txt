

   1. Create a package: mvn package

   2. Upload to S3: mvn install

   3. Deploy Lambda: aws cloudformation deploy --template-file app.yml --stack-name claimsv2  --force-upload
   
   3b. aws lambda update-function-code --function-name ClaimsProcessor --s3-bucket octank-healthcare --s3-key octank-claims-lambda-1.0-SNAPSHOT.jar

   4. Invoke Lambda:

   5. aws lambda invoke \
    --function-name ClaimsProcessor \
    --payload '{ "requestId": "batch-claims", "claimStatus": "Submitted" }' \
    claims.out

   6. Delete Lambda: aws cloudformation delete-stack --stack-name claimsv2

