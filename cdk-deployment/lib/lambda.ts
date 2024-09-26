import * as cdk from 'aws-cdk-lib';
import * as lambda from 'aws-cdk-lib/aws-lambda';
import * as iam from 'aws-cdk-lib/aws-iam';
import { Construct } from 'constructs';

export interface BaseLambdaFunctionStatic {
  generateFunctionProps(props: LambdaProps): lambda.FunctionProps;
  generateAliasOptions(): { name: string, options: lambda.AliasOptions };
  generateEnvironmentVariables(props: LambdaProps, eapiSecretName: string): { [key: string]: string };
}

export function staticImplement<T>() {
  return <U extends T>(constructor: U) => { constructor };
};

export interface LambdaProps extends cdk.StackProps {
  config: {
    subnets: cdk.aws_ec2.ISubnet[];
    securityGroups: cdk.aws_ec2.ISecurityGroup[];
    vpc: cdk.aws_ec2.IVpc;
  }
  vars: {
    eapiName: string;
    env: string;
    project: string;
    lambdaEdbJdbcUrl: string;
    lambdaMainframeJdbcUrl: string;
    lambdaEdbUser: string;
    lambdaMainframeUser: string;
    otlpEndpoint: string;
    profiles: string;
  }
  lambdaExecutionRole: iam.Role;
};

export class LambdaStack extends cdk.Stack {
  constructor(scope: Construct, id: string, props?: LambdaProps) {
    super(scope, id, props);

    // Pull the LambdaExecutionRole & Secret Name
    const eapiSecretName = cdk.Fn.importValue('secretName');

    // For every lambda module specified, create a lambda function and configure them.
    import(`./lambda-funcs/${props?.vars.eapiName}`).then((lambdaFunctionConfig) => {
      const lambdaFunctionProps = lambdaFunctionConfig.LambdaFunction.generateFunctionProps(props);
      const lambdaFunctionAlias = lambdaFunctionConfig.LambdaFunction.generateAliasOptions();

      // Common environment
      const eapi = new lambda.Function(this, `eapi-${props?.vars.eapiName}`, lambdaFunctionProps);

      // Get the environment variables
      for (const [key, value] of Object.entries(lambdaFunctionConfig.LambdaFunction.generateEnvironmentVariables(props, eapiSecretName))) {
        eapi.addEnvironment(key, (value as string));
      }
  
      // From the documentation: The latest version is automatically added to the alias
      eapi.addAlias(lambdaFunctionAlias.name, lambdaFunctionAlias.options);

      // Deletion Policy
      const cfnFunction = eapi.node.defaultChild as lambda.CfnFunction;
      cfnFunction.cfnOptions.deletionPolicy = cdk.CfnDeletionPolicy.RETAIN;
    });
  };
};
