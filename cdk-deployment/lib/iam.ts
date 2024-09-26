import * as cdk from 'aws-cdk-lib';
import * as iam from 'aws-cdk-lib/aws-iam';
import { Construct } from 'constructs';

interface IamStackProps extends cdk.StackProps {
  vars: {
    eapiName: string;
    env: string;
    project: string;
    region: string;
  };
};

export class IamStack extends cdk.Stack {
  private lambdaExecutionRole: iam.Role;

  constructor(scope: Construct, id: string, props?: IamStackProps) {
    super(scope, id, props);

    // Pull the Secret ARN
    const secretArn = cdk.Fn.importValue('secretArn');

    // Define the AWS IAM Role for Lambda Execution
    this.lambdaExecutionRole = new iam.Role(this, 'LambdaExecutionRole', {
      roleName: `${props?.vars.project}-${props?.vars.eapiName}-${props?.vars.env}-lambda-execution-role`,
      assumedBy: new iam.ServicePrincipal('lambda.amazonaws.com'),
    });

    // Attach AWS managed policies to the role
    this.lambdaExecutionRole.addManagedPolicy(iam.ManagedPolicy.fromManagedPolicyArn(this, 'AWSLambdaBasicExecutionRole', 'arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole'));
    this.lambdaExecutionRole.addManagedPolicy(iam.ManagedPolicy.fromManagedPolicyArn(this, 'AWSXRayDaemonWriteAccess', 'arn:aws:iam::aws:policy/AWSXRayDaemonWriteAccess'));
    this.lambdaExecutionRole.addManagedPolicy(iam.ManagedPolicy.fromManagedPolicyArn(this, 'AWSLambdaVPCAccessExecutionRole', 'arn:aws:iam::aws:policy/service-role/AWSLambdaVPCAccessExecutionRole'));

    // Create custom Lambda Secrets Manager policy
    const lambdaSecretsManagerPolicy = new iam.ManagedPolicy(this, 'LambdaSecretsManagerPolicy', {
      managedPolicyName: `${props?.vars.project}-${props?.vars.eapiName}-${props?.vars.env}-lambda-secrets-manager-policy`,
      statements: [
        new iam.PolicyStatement({
          effect: iam.Effect.ALLOW,
          actions: [
            'secretsmanager:GetResourcePolicy',
            'secretsmanager:GetSecretValue',
            'secretsmanager:DescribeSecret',
            'secretsmanager:ListSecretVersionIds'
          ],
          resources: [
            `${secretArn}`,
            `arn:aws:secretsmanager:${props?.vars.region}:${props?.env?.account}:secret:ENTERPRISE_APIS_DB_PWD*`
          ]
        }),
        new iam.PolicyStatement({
          effect: iam.Effect.ALLOW,
          actions: ['secretsManager:ListSecrets'],
          resources: ['*']
        })
      ]
    });

    // Attach custom Lambda Secrets Manager policy to Lambda Execution role
    this.lambdaExecutionRole.addManagedPolicy(iam.ManagedPolicy.fromManagedPolicyName(this, 'LambdaSecretsManagedPolicy', lambdaSecretsManagerPolicy.managedPolicyName));
    
    // Deletion Policy
    const cfnRole = this.lambdaExecutionRole.node.defaultChild as iam.CfnRole;
    cfnRole.cfnOptions.deletionPolicy = cdk.CfnDeletionPolicy.RETAIN;
  };

  getLambdaExecutionRole() {
    return this.lambdaExecutionRole;
  }
};
