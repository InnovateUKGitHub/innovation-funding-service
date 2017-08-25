set -x

# Run the tests in non-GUI mode when not in development
function gui_mode_flag() {
    if [[ ${gui_mode} -eq 0 ]]; then
        echo " -n"
    fi
}

# Get the deviations to apply to the random timers on test steps.  For the Performance environment(s), we want to remove deviation as much as possible to
# allow us to better perform side-by-side analysis of performance on different branches
function test_deviations() {
    if [[ ${include_deviations} -eq 1 ]]; then
        echo "-Jmicro_pause_deviation=100 -Jshort_pause_deviation=500 -Jnormal_pause_deviation=5000 -Jlong_pause_deviation=10000 -Jlonger_pause_deviation=15000"
    else
        echo "-Jmicro_pause_deviation=0 -Jshort_pause_deviation=0 -Jnormal_pause_deviation=0 -Jlong_pause_deviation=0 -Jlonger_pause_deviation=0"
    fi
}

# work out the spread of user journeys to run
function user_journeys() {
    if [[ ${high_load} -eq 1 ]]; then
        echo "-Jinvite_user=0 -Japplicant_users=600 -Jregistration_users=0"
    else
        echo "-Jinvite_user=1 -Japplicant_users=10 -Jregistration_users=1"
    fi
}

# run the tests against the given environment
function perform_tests() {
    rm -f /tmp/${environment}-run.jtl
    jmeter $(gui_mode_flag) -l /tmp/${environment}-run.jtl $(user_journeys) $(test_deviations) -p${properties_file} -tload_test_representative.jmx
}

# the main entry point into this script
function run() {
    echo "This will run the performance tests against ${environment}.  In order to run this, you should ensure that a fresh set of webtest data is available on ${environment} (although these tests themselves are rerunnable on a fresh set)."
    echo ""

    if [[ ${start_prompt} -eq 1 ]]; then

        echo "You should also ensure that the environment is free to use for this purpose.  Please check the #environment-updates channel to see if it reserved at this time."
        echo ""

        read -r -p "Are you happy to run these tests at this time? [y/N]" response

        if [[ $response =~ ^([yY][eE][sS]|[yY])$ ]]; then
            perform_tests
        else
            echo "No problem.  See you next time!"
        fi

    else
        perform_tests
    fi
}

unset environment
unset properties_file
unset gui_mode
unset high_load
unset include_deviations
unset start_prompt

while getopts ":g :l :q :v :e: :p:" opt ; do
    case ${opt} in
        g)
            gui_mode=1
        ;;
        l)
            high_load=1
        ;;
        v)
            include_deviations=1
        ;;    
        q)
            start_prompt=0
        ;;
        e)
            environment="$OPTARG"
        ;;
        p)
            properties_file="$OPTARG"
        ;;
    esac
done

run