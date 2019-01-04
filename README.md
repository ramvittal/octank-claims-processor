# Octank Claims Processor

This Java project was built as a prototype for processing claims using AWS Lambda that perform claims adjudication.
This project uses AWS SAM, AWS Lambda and Hibernate.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

### Prerequisites

1. Execute environment setup instructions mentioned here - https://github.com/ramvittal/octank-claims-master/blob/master/README.md  
2. Checkout this project  
3. Update hibernate.cfg.xml with aurora host dns 
4. Update claimsprocessor.yaml security group id, subnet ids, aurora db conn info



### Installing

Run the folowing commands from your project root  

Build the project using maven
```
mvn package  
```




## Running the tests

 




## Deployment


Package and deploy web apis. Create your own s3 bucket.

```
aws cloudformation package --template-file webapi.yaml --output-template-file output-webapi.yaml --s3-bucket octank-healthcare  
aws cloudformation deploy --template-file output-webapi.yaml --stack-name OctankClaimsWebApis  
```

Package and deploy backsync lambda

```
aws cloudformation package --template-file claimsprocessor.yaml --output-template-file output-claimsprocessor.yaml --s3-bucket octank-healthcare  
aws cloudformation deploy --template-file output-claimsprocessor.yaml --stack-name OctankClaimsProcessor   
```

## Contributing

## Authors

* **Ram Vittal** - *Initial work* - [RamVittal](https://github.com/ramvittal)

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

## Acknowledgments
