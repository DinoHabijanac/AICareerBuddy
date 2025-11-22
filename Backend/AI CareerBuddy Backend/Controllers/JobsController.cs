using AICareerBuddy_BussinesLayer.Interfaces;
using AICareerBuddy_BussinesLayer.Services;
using AICareerBuddy_Entities.Entities;
using Microsoft.AspNetCore.Mvc;

// For more information on enabling Web API for empty projects, visit https://go.microsoft.com/fwlink/?LinkID=397860

namespace AI_CareerBuddy_Backend.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class JobsController : ControllerBase
    {
        private readonly ILogger<JobService> _logger;
        private readonly IJobService JobService;

        public JobsController(ILogger<JobService> logger, JobService jobService)
        {
            _logger = logger;
            JobService = jobService;
        }

        [HttpGet]
        public IEnumerable<JobListing> Get()
        { 
            return JobService.GetJobs(); 
        }
         

        [HttpGet("{id}")]
        public JobListing Get(int id)
        {
            return JobService.GetJob(id);
        }

        [HttpPost]
        public void Post([FromBody] string value)
        {
        }

        [HttpPut("{id}")]
        public void Put(int id, [FromBody] string value)
        {
        }

        [HttpDelete("{id}")]
        public void Delete(int id)
        {
        }
    }
}
