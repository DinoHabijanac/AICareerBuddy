FROM mcr.microsoft.com/dotnet/aspnet:8.0 AS base
WORKDIR /app
EXPOSE 8080

FROM mcr.microsoft.com/dotnet/sdk:8.0 AS build
WORKDIR /src

# Copy project files
COPY ["Backend/AI CareerBuddy Backend/AI CareerBuddy-APILayer.csproj", "Backend/AI CareerBuddy Backend/"]
COPY ["AICareerBuddy-BussinesLayer/AICareerBuddy-BussinesLogicLayer.csproj", "AICareerBuddy-BussinesLayer/"]
COPY ["AICareerBuddy-DataAccessLayer/AICareerBuddy-DataAccessLayer.csproj", "AICareerBuddy-DataAccessLayer/"]
COPY ["AICareerBuddy-Entities/AICareerBuddy-Entities.csproj", "AICareerBuddy-Entities/"]

# Restore dependencies
RUN dotnet restore "Backend/AI CareerBuddy Backend/AI CareerBuddy-APILayer.csproj"

# Copy everything else
COPY . .

# Build
WORKDIR "/src/Backend/AI CareerBuddy Backend"
RUN dotnet build "AI CareerBuddy-APILayer.csproj" -c Release -o /app/build

FROM build AS publish
RUN dotnet publish "AI CareerBuddy-APILayer.csproj" -c Release -o /app/publish /p:UseAppHost=false

FROM base AS final
WORKDIR /app
COPY --from=publish /app/publish .
ENTRYPOINT ["dotnet", "AI CareerBuddy-APILayer.dll"]
