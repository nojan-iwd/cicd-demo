#!/usr/bin/env node
import 'source-map-support/register';
import * as cdk from 'aws-cdk-lib';
import { IamStack } from '../lib/iam';
import { SecretsStack } from '../lib/secrets';
import { LambdaStack } from '../lib/lambda';
import { LookupStack } from '../lib/lookup';

const app = new cdk.App();

// CDK Context for the variables
// "env" should be passed via CLI (env={dev,qa})
const env = app.node.tryGetContext('env');
const vars = {
  env: env,
  project: app.node.tryGetContext('project')[env] ?? app.node.tryGetContext('project'),
  region: app.node.tryGetContext('region')[env] ?? app.node.tryGetContext('region'),
  vpcId: app.node.tryGetContext('vpc_id')[env] ?? app.node.tryGetContext('vpc_id'),
  eapiName: app.node.tryGetContext('eapi_name')[env] ?? app.node.tryGetContext('eapi_name'),
  lambdaEdbJdbcUrl: app.node.tryGetContext('lambda_edb_jdbc_url')[env] ?? app.node.tryGetContext('lambda_edb_jdbc_url'),
  lambdaMainframeJdbcUrl: app.node.tryGetContext('lambda_mainframe_jdbc_url')[env] ?? app.node.tryGetContext('lambda_mainframe_jdbc_url'),
  lambdaEdbUser: app.node.tryGetContext('lambda_edb_user')[env] ?? app.node.tryGetContext('lambda_edb_user'),
  lambdaMainframeUser: app.node.tryGetContext('lambda_mainframe_user')[env] ?? app.node.tryGetContext('lambda_mainframe_user'),
  otlpEndpoint: app.node.tryGetContext('otlp_endpoint')[env] ?? app.node.tryGetContext('otlp_endpoint'),
  profiles: app.node.tryGetContext('profiles')[env] ?? app.node.tryGetContext('profiles'),
  securityGroupId: app.node.tryGetContext('security_group_id')[env] ?? app.node.tryGetContext('security_group_id'),
  subnetIds: app.node.tryGetContext('subnet_ids')[env] ?? app.node.tryGetContext('subnet_ids')
};

// Config Stack (AWS Subnet and Security Group Lookup)
const lookupStack = new LookupStack(app, 'enterprise-apis-lookup-stack', {
  env: {
    account: process.env.CDK_DEFAULT_ACCOUNT,
    region: vars.region
  },
  vars: vars
});

// Module Stacks
const secretsStack = new SecretsStack(app, 'enterprise-apis-secrets-stack', {
  env: {
    account: process.env.CDK_DEFAULT_ACCOUNT,
    region: vars.region
  },
  vars: vars
});

const iamStack = new IamStack(app, `enterprise-apis-iam-${vars.eapiName}-stack`, {
  env: {
    account: process.env.CDK_DEFAULT_ACCOUNT,
    region: vars.region
  },
  vars: vars
});

const lambdaStack = new LambdaStack(app, `enterprise-apis-lambda-${vars.eapiName}-stack`, {
  env: {
    account: process.env.CDK_DEFAULT_ACCOUNT,
    region: vars.region
  },
  config: {
    subnets: lookupStack.getSubnets(),
    securityGroups: lookupStack.getSecurityGroup(),
    vpc: lookupStack.getVpc()
  },
  lambdaExecutionRole: iamStack.getLambdaExecutionRole(),
  vars: vars
});

iamStack.addDependency(secretsStack);
lambdaStack.addDependency(secretsStack);
lambdaStack.addDependency(iamStack);
lambdaStack.addDependency(lookupStack);
