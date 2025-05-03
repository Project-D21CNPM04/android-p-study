package com.example.pstudy.view.result

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.viewModels
import com.example.base.ui.base.BindingActivity
import com.example.pstudy.R
import com.example.pstudy.data.model.Content
import com.example.pstudy.data.model.FlashCard
import com.example.pstudy.data.model.MaterialType
import com.example.pstudy.data.model.MindMap
import com.example.pstudy.data.model.Quiz
import com.example.pstudy.data.model.StudyMaterials
import com.example.pstudy.data.model.Summary
import com.example.pstudy.databinding.ActivityResultBinding
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResultActivity : BindingActivity<ActivityResultBinding>() {

    private val viewModel: ResultViewModel by viewModels()

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, ResultActivity::class.java)
        }
    }

    override fun inflateBinding(layoutInflater: LayoutInflater): ActivityResultBinding {
        return ActivityResultBinding.inflate(layoutInflater)
    }

    override fun updateUI(savedInstanceState: Bundle?) {
        setupToolbar()
        setupViewPagerAndTabs()
        observeViewModel()
        val sampleData = createSampleData()
        viewModel.loadResultData(sampleData)
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.app_name)
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun setupViewPagerAndTabs() {
        val adapter = ResultPagerAdapter(this)
        binding.viewPager.adapter = adapter
        binding.viewPager.isUserInputEnabled = false

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = getTabTitle(position)
        }.attach()
    }

    private fun observeViewModel() {
        // Observe shared ViewModel LiveData if needed for Activity-level UI changes
        // Example:
        // viewModel.resultTitle.observe(this) { title ->
        //     supportActionBar?.title = title
        // }
    }

    private fun getTabTitle(position: Int): String? {
        return when (position) {
            0 -> getString(R.string.tab_summary)
            1 -> getString(R.string.tab_mind_map)
            2 -> getString(R.string.tab_flashcards)
            3 -> getString(R.string.tab_quizs)
            else -> null
        }
    }

    private fun createSampleData(): StudyMaterials {
        val summary = Summary(
            id = "summary-1",
            noteId = "note-1",
            content = """
                # Main Points
                
                ## Introduction to Kotlin
                Kotlin is a modern programming language that makes developers happier.
                
                ## Key Features
                - Null safety
                - Extension functions
                - Coroutines for asynchronous programming
                - Concise syntax
                
                ## Benefits
                1. Fully interoperable with Java
                2. Reduces boilerplate code
                3. Helps prevent common programming errors
                
                > Kotlin is the preferred language for Android development
            """.trimIndent()
        )

        val mindMap = MindMap(
            id = "mindmap-1",
            content = """
                <!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Markmap</title>
    <style>
      svg.markmap {
        width: 100%;
        height: 100vh;
      }
    </style>
    <script src="https://cdn.jsdelivr.net/npm/markmap-autoloader@0.18"></script>
  </head>
  <body>
    <div class="markmap">
      <script type="text/template">
        # Service-Oriented Software Development

        ## Learning Outcomes
        
        *   Understanding:
            *   Fundamental concepts, theories, and techniques in service-oriented computing.
            *   Analysis and modeling service use SOAP and REST.
            *   Design patterns and service paradigms for web service development.
        *   Skills:
            *   Analyze, design, and implement service-oriented software based on SOAP or REST.
        
        ## Outlines
        
        *   Introduction to Service-Orientation
        *   Building up the Service-Oriented Solution
        *   Analysis and Modeling Service
        *   Service API and Contract Design
        *   Service API and Contract Versioning
        *   Service-Oriented Analysis and Design Fundamentals
        *   Microservice Pattern
        *   Communication patterns
        *   Managing transactions with sagas
        *   Domain-driven design (DDD) aggregate pattern
        *   Event sourcing pattern
        *   API composition pattern or Command query responsibility segregation (CQRS) pattern
        *   External API patterns
        
        ## Textbooks
        
        *   **Required:**
            *   Thomas Erl, "Analysis and Design for Services and Microservices", 2nd edition, 2017.
            *   Chris Richardson, "Microservices patterns With Examples in Java", 2019.
        *   **Optional:**
            *   Sam Newman. Building Microservices, 2nd Edition. 2021.
            *   M. Papazoglou. Web Services and SOA: Principles and Technology, 2nd Edition. 2012.
            *   Robert Daigneau. Service Design Patterns: Fundamental Design Solutions for SOAP/WSDL and RESTful Web Services. 2011.
        
        ## Grading Policy
        
        *   Attendance: 10% (Individual)
        *   Mid-term projects/exams: 20% (Individual)
        *   Exercises: 20% (Group, max 3)
        *   Final examination (lab): 50% (Individual)
        *   Q & A: Individual
        
        ## History of Microservices
        
        *   Late 1990s - Early 2000s: Roots in Service-Oriented Architecture (SOA)
        *   1997: Enterprise Java Beans (EJB) as a forerunner
        *   1999/2005: Shift from SOAP to REST
        *   2011-2012: Birth of Microservices
        *   Present/Future: Continued evolution and adoption
        
        ## Monolithic vs. Microservices
        
        *   **Monolithic Architecture:**
            *   Unified, self-contained unit.
            *   Single code base.
            *   Released at once.
            *   **Advantages:** Easy deployment, development, performance, simplified testing, easy debugging. 
            *   **Disadvantages:** Slower development, scalability issues, reliability concerns, barrier to technology adoption, lack of flexibility, deployment cost.
        *   **Microservices Architecture:**
            *   Independently deployable services.
            *   Own business logic and database.
            *   Independent lifecycle (dev, test, deploy, scale, maintenance).
            *   Often paired with DevOps and Continuous Delivery (CD).
            *   **Advantages:** Agility, flexible scaling, continuous deployment, maintainability, testability, independent deployment, technology flexibility, high reliability, happier teams.
            *   **Disadvantages:** Development sprawl, infrastructure costs, organizational overhead, debugging challenges, lack of standardization, lack of clear ownership.
        
        ## Principles for Microservice Architecture Design
        
        *   Single responsibility
        *   Focused on business capabilities
        *   Designed for failure
        
        ## Case Studies
        
        *   **Amazon:**
            *   Problem: Scaling challenges with a monolithic application.
            *   Solution: Transition to a service-oriented architecture (precursor to microservices).
        *   **Netflix:**
            *   Problem: Growing pains with a monolithic architecture.
            *   Solution: Migrated to a cloud-based microservices architecture.
        *   **Atlassian (Jira & Confluence):**
            *   Problem: Scaling challenges.
            *   Solution: Transformed to a multi-tenant, stateless cloud application with microservices.       
        *   **Carpooling/Ridesharing (Lyft/Uber):**
            *   Problem: Fast-growing user base and monolithic scaling problems.
        *   **Etsy:**
            *   Problem: Performance issues.
            *   Solution: Microservices architecture.
        *   **Spotify:**
            *   Problem: Data center maintenance, developer resource allocation, leveraging Google Cloud innovations.
            *   Solution: Migration to Google Cloud using microservices.
        
        ## Roadmap
        
        *   Refer to external resources for microservices and DevOps roadmaps.
        
        ## References
        
        *   List of URLs provided in the original text.
      </script>
    </div>
  </body>
</html>
            """.trimIndent(),
            summary = "A mind map about Kotlin programming language features"
        )

        val flashCards = listOf(
            FlashCard(
                id = 1,
                title = "Basic Kotlin",
                content = Content(
                    front = "What is a key feature of Kotlin that helps prevent NullPointerException?",
                    back = "Null safety with the safe call operator (?.) and the not-null assertion operator (!!)."
                )
            ),
            FlashCard(
                id = 2,
                title = "Coroutines",
                content = Content(
                    front = "What Kotlin feature is designed to simplify asynchronous programming?",
                    back = "Coroutines, which allow asynchronous code to be written in a sequential style."
                )
            ),
            FlashCard(
                id = 3,
                title = "Extension Functions",
                content = Content(
                    front = "How do you add new functionality to existing classes without inheritance?",
                    back = "Using extension functions, which allow you to extend a class with new functionality without inheriting from it."
                )
            )
        )

        val quizzes = listOf(
            Quiz(
                id = 1,
                questions = "Which keyword is used to define a variable that cannot be reassigned?",
                choices = listOf("val", "var", "const", "final"),
                answer = "val"
            ),
            Quiz(
                id = 2,
                questions = "Which of these is NOT a benefit of using Kotlin?",
                choices = listOf(
                    "Interoperability with Java",
                    "Requires more code than Java",
                    "Built-in null safety",
                    "Support for functional programming"
                ),
                answer = "Requires more code than Java"
            ),
            Quiz(
                id = 3,
                questions = "What is the correct way to create an immutable list in Kotlin?",
                choices = listOf(
                    "listOf(1, 2, 3)",
                    "mutableListOf(1, 2, 3)",
                    "arrayListOf(1, 2, 3)",
                    "arrayOf(1, 2, 3).toList()"
                ),
                answer = "listOf(1, 2, 3)"
            )
        )

        return StudyMaterials(
            id = "sample-1",
            input = "Kotlin Programming",
            type = MaterialType.TEXT,
            userId = "user-123",
            summary = summary,
            mindMap = mindMap,
            flashCards = flashCards,
            quizzes = quizzes
        )
    }
}