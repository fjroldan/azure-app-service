# azure-app-service

dotnet new webapi -o TodoApi
cd TodoApi
dotnet add package Microsoft.EntityFrameworkCore.InMemory
dotnet run

az ad sp create-for-rbac --role="Contributor" --scopes="/subscriptions/bcb520ca-0fe0-4eaa-9afc-da74b5c01bd7"
az account list

az login

# Tuto
https://learn.microsoft.com/en-us/azure/app-service/provision-resource-terraform

# Repo example
https://github.com/Azure-Samples/nodejs-docs-hello-world

# planes de Azure App Service
https://learn.microsoft.com/es-es/azure/app-service/overview-hosting-plans

# CI-CD
https://registry.terraform.io/providers/hashicorp/azurerm/latest/docs/resources/app_service_source_control

# Resourse
https://learn.microsoft.com/en-us/azure/app-service/samples-resource-manager-templates

## Comandos
- Para verificar el código
```
terraform fmt -recursive
```
- Para generar un plan desde archivo de confuguración
```
terraform plan -var-file infrastructure/pre.auto.tfvars.json -out main.tfplan
```
- Para aplicar un plan desde archivo de confuguración
```
terraform apply -var-file infrastructure/pre.auto.tfvars.json -auto-approve 
```
- Para actualizar el provider lib
```
terraform init -upgrade
```
- Para eliminar todos los recursos
```
terraform apply -destroy -var-file infrastructure/ws.auto.tfvars.json
```
- Ejemplo de creación de una máquina virtual con sistema operativo con hardering.
```
terraform apply -destroy -var-file infrastructure/red_hat_vm.auto.tfvars.json
```

