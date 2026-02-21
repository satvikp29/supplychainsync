# Terraform – AWS (ECR)

Creates ECR repositories for SupplyChainSync container images. Use with GitHub Actions to push images after build.

## Prerequisites

- AWS CLI configured (credentials or IAM role)
- Terraform >= 1.5

## Usage

1. Copy and customize (optional):
   ```bash
   cp terraform.tfvars.example terraform.tfvars
   ```

2. If using remote state, create an S3 bucket and DynamoDB table, then uncomment and set the `backend "s3"` block in `main.tf`. Otherwise remove the backend block to use local state.

3. Apply:
   ```bash
   terraform init
   terraform plan
   terraform apply
   ```

4. Use the output `ecr_*_url` values in CI/CD to tag and push images.
