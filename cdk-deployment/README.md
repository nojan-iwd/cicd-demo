# Enterprise API AWS Deployment using CDK TypeScript

The `cdk.json` file tells the CDK Toolkit how to execute your app. This is the same file where the environment variables are specified.

## Useful commands

* `npm run build`   compile typescript to js
* `npm run watch`   watch for changes and compile
* `npm run test`    perform the jest unit tests
* `npx cdk deploy`  deploy this stack to your default AWS account/region
* `npx cdk diff`    compare deployed stack with current state
* `npx cdk synth`   emits the synthesized CloudFormation template

Provide a `-c env=dev` flag to each of the commands to pull all of the configured variables for the `dev` environment. Likewise, the same can be done for `qa`, `sandbox`, etc.

## Repository Structure

The `bin` directory essentially holds the entrypoint file. This is configured in `cdk.json`. The `lib` directory is where all of our stacks are defined. For the lambda function modules, they all inherit from the `base` directory and provided their respective properties. This is then retrieved by the generic `LambdaStack` class which will package everything.
