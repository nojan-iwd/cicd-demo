import * as cdk from 'aws-cdk-lib';
import { SecurityGroup, ISecurityGroup, ISubnet, IVpc, Vpc } from 'aws-cdk-lib/aws-ec2';
import { Construct } from 'constructs';

interface LookupProps extends cdk.StackProps {
  vars: {
    securityGroupId: string;
    subnetIds: string[];
    vpcId: string;
  }
};

export class LookupStack extends cdk.Stack {
  private vpc: IVpc;
  private securityGroups: ISecurityGroup[];
  private subnets: ISubnet[];

  constructor(scope: Construct, id: string, props?: LookupProps) {
    super(scope, id, props);

    this.vpc = Vpc.fromLookup(this, 'eapiVpc', {
      vpcId: props?.vars.vpcId
    });

    // Lookup the AWS Subnets and AWS Security Groups
    this.securityGroups = [SecurityGroup.fromLookupById(this, 'eapiSecurityGroup', `${props?.vars.securityGroupId}`)];

    const subnetIds: string[] = props?.vars.subnetIds ?? [];

    // https://github.com/aws/aws-cdk/issues/19786#issuecomment-1718212794
    this.subnets = subnetIds.map(subnetId => {
      let subnetObj = cdk.aws_ec2.Subnet.fromSubnetId(this, subnetId, subnetId);
      cdk.Annotations.of(subnetObj).acknowledgeWarning('@aws-cdk/aws-ec2:noSubnetRouteTableId');
      return subnetObj;
    });
  };

  getSecurityGroup() {
    return this.securityGroups;
  };

  getSubnets() {
    return this.subnets;
  };

  getVpc() {
    return this.vpc;
  }
};
