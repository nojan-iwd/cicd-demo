import * as cdk from 'aws-cdk-lib';
import * as secretsmanager from 'aws-cdk-lib/aws-secretsmanager';
import { Construct } from 'constructs';

interface SecretsStackProps extends cdk.StackProps {
  vars: {
    env: string;
  }
};

export class SecretsStack extends cdk.Stack {
  private secret: secretsmanager.Secret;
  
  constructor(scope: Construct, id: string, props?: SecretsStackProps) {
    super(scope, id, props);

    this.secret = new secretsmanager.Secret(this, 'EapiCommonSecrets', {
      secretName: `enterprise-apis/${props?.vars.env}/common-secrets`,
      description: 'For shared secrets among Enterprise APIs',
    });

    // Outputs
    new cdk.CfnOutput(this, "secretName", {
      value: this.secret.secretName,
      exportName: "secretName",
    });

    new cdk.CfnOutput(this, "secretArn", {
      value: this.secret.secretArn,
      exportName: "secretArn",
    });
  };
};
