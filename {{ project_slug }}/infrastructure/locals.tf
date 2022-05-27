locals {
  k8s_namespace       = "dge"
  k8s_service_account = "{{ project_slug }}"
  team                = "streams"
  component           = "{{ project_slug }}"

  ## The provider should not have `https://`, see https://docs.aws.amazon.com/eks/latest/userguide/create-service-account-iam-policy-and-role.html#create-service-account-cli
  oidc_provider = replace(element(data.terraform_remote_state.eks_cluster.outputs.cluster-identity.oidc.*.issuer, 0), "https://", "")
}
