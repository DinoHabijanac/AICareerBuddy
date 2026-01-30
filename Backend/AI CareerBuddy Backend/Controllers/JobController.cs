using AICareerBuddy_BussinesLayer.Interfaces;
using AICareerBuddy_BussinesLayer.Services;
using AICareerBuddy_Entities.Entities;
using Microsoft.AspNetCore.Mvc;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace AI_CareerBuddy_Backend.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class JobController : ControllerBase
    {
        private readonly IJobService JobService;

        public JobController(IJobService jobService)
        {
            JobService = jobService;
        }

        // GET: api/Job
        [HttpGet]
        public async Task<ActionResult<IEnumerable<JobListing>>> Get()
        {
            var jobs = await JobService.GetJobs();
            if (jobs != null) return Ok(jobs);
            else return NotFound();
        }

        // GET api/Job/5
        [HttpGet("{id}")]
        public async Task<ActionResult<JobListing>> Get(int id)
        {
            var job = await JobService.GetJob(id);
            if (job != null) return Ok(job);
            else return NotFound();
        }


        // GET api/Job/user/5
        [HttpGet("user/{userId}")]
        public async Task<ActionResult<IEnumerable<JobListing>>> GetByUserId(int userId)
        {
            if (userId == 0) return BadRequest();
            var jobs = await JobService.GetJobsByUserId(userId);
            if (jobs != null && jobs.Count > 0) return Ok(jobs);
            else return NotFound();
        }


        // POST api/Job
        [HttpPost]
        public async Task<ActionResult<JobListing>> Post([FromBody] JobListing job)
        {
            var result = DateOnly.TryParse(job.ListingExpires.ToString(), out var dt);
            if(result) job.ListingExpires = dt;
            else job.ListingExpires = DateOnly.FromDateTime(DateTime.Now);

            var isCreated = await JobService.PostJob(job);
            if (isCreated) return Created();
            else return BadRequest();
        }

        [HttpGet("student/{id}")]
        public async Task<ActionResult<User>> GetStudent(int id)
        {
            if (id == 0) return BadRequest();
            var student = await JobService.GetStudentById(id);
            if (student != null) return Ok(new { name = student.FirstName, lastname = student.LastName });
            else return NotFound();
        }

        // DELETE api/Job/5
        [HttpDelete("{id}")]
        public async Task<ActionResult> Delete(int id)
        {
            var deleted = await JobService.DeleteJob(id);
            if (deleted) return NoContent();
            else return NotFound();
        }
    }
}