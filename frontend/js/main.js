console.log("BusGo main.js loaded");
const API_BASE_URL = "http://localhost:8080/api";


/* ================= BUS SEARCH ================= */

const searchForm = document.getElementById("searchForm");

if (searchForm) {

    searchForm.addEventListener("submit", async function (event) {

        event.preventDefault();


        // Get form values

        const source =
            document.getElementById("source").value.trim();

        const destination =
            document.getElementById("destination").value.trim();

        const journeyDate =
            document.getElementById("journeyDate").value;


        // Basic validation

        if (!source || !destination || !journeyDate) {

            alert("Please fill all search fields.");

            return;
        }


        // Save search details

        localStorage.setItem(
            "busgo_source",
            source
        );

        localStorage.setItem(
            "busgo_destination",
            destination
        );

        localStorage.setItem(
            "busgo_date",
            journeyDate
        );


        // Show loading message

        const button =
            searchForm.querySelector("button[type='submit']");

        const originalText =
            button.innerHTML;

        button.disabled = true;

        button.innerHTML = `
            <span class="spinner-border spinner-border-sm me-2"></span>
            Searching...
        `;


        try {

            const response = await fetch(
                `${API_BASE_URL}/routes/search?source=${encodeURIComponent(source)}&destination=${encodeURIComponent(destination)}&date=${journeyDate}`
            );


            if (!response.ok) {

                throw new Error(
                    "Unable to search buses."
                );

            }


            const routes =
                await response.json();


            // Save search results

            localStorage.setItem(
                "busgo_routes",
                JSON.stringify(routes)
            );


            // Redirect to results page

            window.location.href =
                "bus-results.html";


        }

        catch (error) {

            console.error(
                "Bus search error:",
                error
            );

            alert(
                "Unable to search buses. Please make sure the backend is running."
            );


            button.disabled = false;

            button.innerHTML =
                originalText;

        }

    });

}