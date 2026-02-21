output "ecr_inventory_service_url" {
  description = "ECR repository URL for inventory-service"
  value       = aws_ecr_repository.inventory_service.repository_url
}

output "ecr_shipment_service_url" {
  description = "ECR repository URL for shipment-service"
  value       = aws_ecr_repository.shipment_service.repository_url
}

output "ecr_login_command" {
  description = "AWS CLI command to authenticate Docker to ECR"
  value       = "aws ecr get-login-password --region ${var.aws_region} | docker login --username AWS --password-stdin ${split("/", aws_ecr_repository.inventory_service.repository_url)[0]}"
  sensitive   = false
}
