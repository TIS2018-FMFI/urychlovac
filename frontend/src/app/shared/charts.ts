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
            fontSize: '13px'
          }
        }
      },
      yAxis: {
        title: {
          text: '',
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
            pointFormat: '{point.x} , {point.y} '
          }
        }
      },
      series: [{
        //name: 'Female',
        color: 'rgba(223, 83, 83, .5)',
        data: []
      }]
}

