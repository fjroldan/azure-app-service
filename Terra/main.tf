# Configure the Azure provider
terraform {
  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = "~> 3.0.0"
    }
  }
  required_version = ">= 0.14.9"
}

provider "azurerm" {
  features {}
  subscription_id = "bcb520ca-0fe0-4eaa-9afc-da74b5c01bd7"
  client_id       = "aeeafcad-0b41-4f28-8c72-129641a4d94b"
  client_secret   = "oIw8Q~oEc13kxK0aJ8OUMs~cd2~OjktsDwWxKcxd"
  tenant_id       = "1acfa458-2c17-44e5-b9d8-ff851b6c7c5f"
}

# Generate a random integer to create a globally unique name
resource "random_integer" "ri" {
  min = 10000
  max = 99999
}

# Create the resource group
resource "azurerm_resource_group" "rg" {
  name     = "myResourceGroup-${random_integer.ri.result}"
  location = "West Europe"

  tags = {
    environment = "dev"
  }
}

# Create the Linux App Service Plan
resource "azurerm_service_plan" "appserviceplan" {
  name                = "webapp-asp-${random_integer.ri.result}"
  location            = azurerm_resource_group.rg.location
  resource_group_name = azurerm_resource_group.rg.name
  os_type             = "Linux"
  sku_name            = "B1"
}

# Create the web app, pass in the App Service Plan ID
resource "azurerm_linux_web_app" "webapp" {
  name                  = "webapp-${random_integer.ri.result}"
  location              = azurerm_resource_group.rg.location
  resource_group_name   = azurerm_resource_group.rg.name
  service_plan_id       = azurerm_service_plan.appserviceplan.id
  https_only            = false
  site_config {
    application_stack {
      dotnet_version = "6.0"
    }
    app_command_line = "dotnet run"
  }
}

#  Deploy code from a public GitHub repo
resource "azurerm_app_service_source_control" "sourcecontrol" {
  app_id             = azurerm_linux_web_app.webapp.id
  repo_url           = "https://github.com/fjroldan/azure-app-service.git"
  branch             = "main"
  use_manual_integration = true
  use_mercurial      = false
}