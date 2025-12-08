using System;
using System.Collections.Generic;

namespace AICareerBuddy_Entities.Entities
{
    public partial class User
    {
        public int Id { get; set; }

        public string FirstName { get; set; }

        public string LastName { get; set; }

        public string Username { get; set; }

        public string Email { get; set; }

        public string Password { get; set; }

        public string Role { get; set; }

        public DateTime CreatedAt { get; set; }

        // Navigation properties
        public virtual ICollection<JobListing> JobListings { get; set; } = new List<JobListing>();

        public virtual ICollection<ResumeFileInfo> ResumeFileInfos { get; set; } = new List<ResumeFileInfo>();
    }
}
