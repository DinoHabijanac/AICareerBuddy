using AICareerBuddy_BussinesLayer.Interfaces;
using AICareerBuddy_Entities.Entities;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace AICareerBuddy_BussinesLayer.Services
{
    public class JobService : IJobService
    {
        public JobListing DeleteJob(int id)
        {
            throw new NotImplementedException();
        }

        public JobListing DeleteJob(string jobName)
        {
            throw new NotImplementedException();
        }

        public JobListing GetJob(int id)
        {
            //TODO: IMPLEMENTIRAT VRAĆANJE IZ BAZE
            return new JobListing()
            {
                Id = id,
                Name = "Junior .NET Developer",
                Description = "Work on backend features, write unit tests and fix bugs in a .NET 8 codebase.",
                Category = "Software Development",
                Location = "Zagreb, Croatia (Hybrid)",
                ListingExpires = DateTime.UtcNow.AddDays(30),
                Terms = new List<string> { "Full-time", "Hybrid", "Junior" }
            };
        }

        public JobListing GetJob(string jobName)
        {
            throw new NotImplementedException();
        }

        public List<JobListing> GetJobs()
        {
            //TODO: IMPLEMENTIRAT VRAĆANJE IZ BAZE
            return new List<JobListing>
            {
                new JobListing
                {
                    Name = "Junior .NET Developer",
                    Description = "Work on backend features, write unit tests and fix bugs in a .NET 8 codebase.",
                    Category = "Software Development",
                    Location = "Zagreb, Croatia (Hybrid)",
                    ListingExpires = DateTime.UtcNow.AddDays(30),
                    Terms = new List<string> { "Full-time", "Hybrid", "Junior" }
                },
                new JobListing
                {
                    Name = "Frontend Engineer (React)",
                    Description = "Build responsive UI components and collaborate with designers to improve UX.",
                    Category = "Frontend Development",
                    Location = "Remote",
                    ListingExpires = DateTime.UtcNow.AddDays(25),
                    Terms = new List<string> { "Full-time", "Remote", "Mid-level" }
                },
                new JobListing
                {
                    Name = "Data Analyst Intern",
                    Description = "Support data collection and analysis, prepare dashboards and reports using SQL and Power BI.",
                    Category = "Data & Analytics",
                    Location = "Split, Croatia (On-site)",
                    ListingExpires = DateTime.UtcNow.AddDays(45),
                    Terms = new List<string> { "Internship", "On-site", "Part-time" }
                },
                new JobListing
                {
                    Name = "DevOps Engineer",
                    Description = "Maintain CI/CD pipelines, infrastructure-as-code and monitor cloud services (Azure).",
                    Category = "DevOps",
                    Location = "Zagreb, Croatia (On-site)",
                    ListingExpires = DateTime.UtcNow.AddDays(20),
                    Terms = new List<string> { "Full-time", "On-site", "Experienced" }
                },
                new JobListing
                {
                    Name = "Product Manager",
                    Description = "Define product roadmap, gather requirements and coordinate cross-functional teams.",
                    Category = "Product",
                    Location = "Hybrid / Remote",
                    ListingExpires = DateTime.UtcNow.AddDays(60),
                    Terms = new List<string> { "Full-time", "Hybrid", "Senior" }
                }
            };
        }

        public JobListing PostJob(JobListing jobListing)
        {
            throw new NotImplementedException();
        }

        public JobListing PutJob(JobListing jobListing)
        {
            throw new NotImplementedException();
        }
    }
}
