resource "aws_iam_role" "{{ project_slug }}" {
  name               = "{{ project_slug }}"
  assume_role_policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "Federated": "arn:aws:iam::${lookup(var.account_id, terraform.workspace)}:oidc-provider/${local.oidc_provider}"
      },
      "Action": "sts:AssumeRoleWithWebIdentity",
      "Condition": {
        "StringEquals": {
          "${local.oidc_provider}:sub": "system:serviceaccount:${local.k8s_namespace}:${local.k8s_service_account}"
        }
      }
    }
  ]
}
EOF
}

resource "aws_iam_role_policy" "{{ project_slug }}" {
  name = "{{ project_slug }}"
  role = aws_iam_role.{{ project_slug }}.id

  policy = <<EOF
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Action": [
                "states:StartExecution",
                "states:SendTaskSuccess",
                "states:GetExecutionHistory"
            ],
            "Effect": "Allow",
            "Resource": "*"
        }
    ]
}
EOF
}
