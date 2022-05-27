terraform {
  backend "s3" {
    bucket = "imgarena-terraform-state"
    key    = "streams-{{ project_slug }}/terraform.tfstate"
    region = "eu-west-1"

  }
  required_version = "1.2.1"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "3.71.0"
    }
    local = {
      source  = "hashicorp/local"
      version = "2.2.2"
    }
  }
}

variable "account_id" {
  type = map(string)
  default = {
    streams-dev     = 810312485871
    streams-staging = 828720528568
    streams-prod    = 313612111793
  }
}

provider "aws" {

  region = "eu-west-1"

  assume_role {
    role_arn     = "arn:aws:iam::${lookup(var.account_id, terraform.workspace)}:role/${terraform.workspace}-cicd"
    session_name = "terraform-streams-{{ project_slug }}"
  }
}

provider "local" {}

data "terraform_remote_state" "eks_cluster" {
  backend = "s3"
  config = {
    bucket = "imgarena-terraform-state"
    key    = "aws-eks-cluster/terraform.tfstate"
    region = "eu-west-1"

  }
  workspace = terraform.workspace
}
