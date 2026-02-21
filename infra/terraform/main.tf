terraform {
  required_version = ">= 1.5"
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
  # Uncomment and configure for remote state:
  # backend "s3" {
  #   bucket         = "your-terraform-state-bucket"
  #   key            = "supplychainsync/terraform.tfstate"
  #   region         = "us-east-1"
  #   dynamodb_table = "terraform-lock"
  # }
}

provider "aws" {
  region = var.aws_region
}

# ECR repositories for container images
resource "aws_ecr_repository" "inventory_service" {
  name                 = "supplychainsync/inventory-service"
  image_tag_mutability = "MUTABLE"
  image_scanning_configuration {
    scan_on_push = true
  }
}

resource "aws_ecr_repository" "shipment_service" {
  name                 = "supplychainsync/shipment-service"
  image_tag_mutability = "MUTABLE"
  image_scanning_configuration {
    scan_on_push = true
  }
}

# Optional: ECR lifecycle policy to limit stored images
resource "aws_ecr_lifecycle_policy" "inventory_service" {
  repository = aws_ecr_repository.inventory_service.name
  policy     = jsonencode({
    rules = [{
      rulePriority = 1
      description  = "Keep last 10 images"
      selection = {
        tagStatus   = "any"
        countType   = "imageCountMoreThan"
        countNumber = 10
      }
      action = { type = "expire" }
    }]
  })
}

resource "aws_ecr_lifecycle_policy" "shipment_service" {
  repository = aws_ecr_repository.shipment_service.name
  policy     = jsonencode({
    rules = [{
      rulePriority = 1
      description  = "Keep last 10 images"
      selection = {
        tagStatus   = "any"
        countType   = "imageCountMoreThan"
        countNumber = 10
      }
      action = { type = "expire" }
    }]
  })
}
