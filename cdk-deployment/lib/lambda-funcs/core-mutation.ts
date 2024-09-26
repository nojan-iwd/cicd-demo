import * as cdk from 'aws-cdk-lib';
import * as lambda from 'aws-cdk-lib/aws-lambda';
import { LambdaProps } from '../lambda';
import { BaseLambdaFunctionStatic, staticImplement } from '../lambda';

@staticImplement<BaseLambdaFunctionStatic>()
export class LambdaFunction {
  static generateFunctionProps(props: LambdaProps) : lambda.FunctionProps {
    return {
      functionName: `${props?.vars.project}-core-mutation-${props?.vars.env}-lambda-function`,
      role: props?.lambdaExecutionRole,
      runtime: lambda.Runtime.JAVA_21,
      handler: 'org.springframework.cloud.function.adapter.aws.FunctionInvoker::handleRequest',
      code: lambda.Code.fromAsset(`assets/${props?.vars.env}/core-0.0.1-SNAPSHOT-aws.jar`),
      timeout: cdk.Duration.seconds(30),
      memorySize: 512,
      ephemeralStorageSize: cdk.Size.mebibytes(512),
      snapStart: lambda.SnapStartConf.ON_PUBLISHED_VERSIONS,
      vpc: props?.config.vpc,
      vpcSubnets: {
        subnets: props?.config.subnets,
      },
      securityGroups: props?.config.securityGroups,
      tracing: lambda.Tracing.ACTIVE
    };
  };

  static generateAliasOptions() : { name: string, options: lambda.AliasOptions } {
    return {
      name: 'live',
      options: {
        description: 'Alias for core mutation'
      }
    };
  };

  static generateEnvironmentVariables(props: LambdaProps, eapiSecretName: string) : { [key: string]: string } {
    return {
      ENTERPRISE_API_SECRET_NAME: `${eapiSecretName}`,
      ENTERPRISE_EDB_URL: `${props?.vars.lambdaEdbJdbcUrl}`,
      ENTERPRISE_MAINFRAME_URL: `${props?.vars.lambdaMainframeJdbcUrl}`,
      ENTERPRISE_EDB_USER: `${props?.vars.lambdaEdbUser}`,
      ENTERPRISE_MAINFRAME_USER: `${props?.vars.lambdaMainframeUser}`,
      ENV: `${props?.vars.env}`,
      SPRING_PROFILES_ACTIVE: `${props?.vars.profiles}`,
      OTLP_ENDPOINT: `${props?.vars.otlpEndpoint}`,
    };
  }
};
