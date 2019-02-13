export const CHART_BLUE_COLORS = [
    '#2e3192',
    '#0071bc',
    '#29abe2',
    '#7fcdee',
    '#bdccd4',
    '#f7b43e',
    '#f7821e',
    '#f15a24',
    '#f1b594',
    '#cece20',
    '#8cc63f',
    '#22b573',
    '#009245',
    '#006837',
    '#149485',
  ];
  
export const LINE_CHART = {
  chart: {
      type: 'line'
    },
    title: {
      text: 'Monthly Average Temperature'
    },
    xAxis: {
      categories: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec']
    },
    yAxis: {
      title: {
        text: 'Temperature (°C)'
      }
    },
    plotOptions: {
      line: {
        dataLabels: {
          enabled: true
        },
        enableMouseTracking: true
      }
    },
    series: [{
      name: 'Tokyo',
      data: [7.0, 6.9, 9.5, 14.5, 18.4, 21.5, 25.2, 26.5, 23.3, 18.3, 13.9, 9.6]
    }, {
      name: 'London',
      data: [3.9, 4.2, 5.7, 8.5, 11.9, 15.2, 17.0, 16.6, 14.2, 10.3, 6.6, 4.8]
    }]
}


export const SCATTER_CHART = {
    chart: {
        type: 'scatter',
        zoomType: 'xy'
      },
      title: {
        text: '',
        enabled: false,
        style: {
          fontSize: '29px'
        },
      },
      
      xAxis: {
        type: 'datetime',
        title: {
          enabled: true,
          text: 'Time',
          style: {
            fontSize: '15px'
          }
        },
        startOnTick: true,
        endOnTick: true,
        showLastLabel: true,
        labels: {
          style: {
            fontSize: '15px'
          }
        }
      },
      yAxis: {
        title: {
          text: 'Weight (kg)',
          style: {
            fontSize: '15px'
          }
        },
        labels: {
          style: {
            fontSize: '15px'
          }
        }
      },
      legend: {
        enabled: false,
        layout: 'vertical',
        align: 'left',
        verticalAlign: 'top',
        x: 100,
        y: 70,
        floating: true,
        backgroundColor: '#FFFFFF',
        borderWidth: 1
      },
      plotOptions: {
        scatter: {
          marker: {
            radius: 5,
            states: {
              hover: {
                enabled: true,
                lineColor: 'rgb(100,100,100)'
              }
            }
          },
          states: {
            hover: {
              marker: {
                enabled: false
              }
            }
          },
          tooltip: {
            // headerFormat: '<b>{series.name}</b><br>',
            pointFormat: '{point.x} cm, {point.y} kg'
          }
        }
      },
      series: [{
        name: 'Female',
        color: 'rgba(223, 83, 83, .5)',
        data: []
      }]
}

