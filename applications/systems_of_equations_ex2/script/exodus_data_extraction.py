import sys, os
#### import the simple module from the paraview
from paraview.simple import *

if __name__ == "__main__" and len(sys.argv) > 1:
    time_step = int(sys.argv[1])

    #### disable automatic camera reset on 'Show'
    paraview.simple._DisableFirstRenderCameraReset()

    # create a new 'ExodusIIReader'
    oute = ExodusIIReader(FileName=['./out.e'])
    timestep_values = oute.TimestepValues
    oute.PointVariables = []
    oute.SideSetArrayStatus = []

    # get animation scene
    animationScene1 = GetAnimationScene()

    # update animation scene based on data timesteps
    animationScene1.UpdateAnimationUsingDataTimeSteps()

    # Properties modified on oute
    oute.PointVariables = ['vel_', 'p']
    oute.ElementBlocks = ['Unnamed block ID: 0 Type: QUAD9']

    # get active view
    renderView1 = GetActiveViewOrCreate('RenderView')
    renderView1.ViewTime = timestep_values[time_step - 1]
    # uncomment following to set a specific view size
    # renderView1.ViewSize = [1611, 832]

    # show data in view
    outeDisplay = Show(oute, renderView1)
    # trace defaults for the display properties.
    outeDisplay.ColorArrayName = [None, '']
    outeDisplay.OSPRayScaleArray = 'GlobalNodeId'
    outeDisplay.OSPRayScaleFunction = 'PiecewiseFunction'
    outeDisplay.SelectOrientationVectors = 'GlobalNodeId'
    outeDisplay.ScaleFactor = 0.1
    outeDisplay.SelectScaleArray = 'GlobalNodeId'
    outeDisplay.GlyphType = 'Arrow'
    outeDisplay.ScalarOpacityUnitDistance = 0.19193831036664846
    outeDisplay.GaussianRadius = 0.05
    outeDisplay.SetScaleArray = ['POINTS', 'GlobalNodeId']
    outeDisplay.ScaleTransferFunction = 'PiecewiseFunction'
    outeDisplay.OpacityArray = ['POINTS', 'GlobalNodeId']
    outeDisplay.OpacityTransferFunction = 'PiecewiseFunction'

    # reset view to fit data
    renderView1.ResetCamera()

    #changing interaction mode based on data extents
    renderView1.InteractionMode = '2D'
    renderView1.CameraPosition = [0.5, 0.5, 10000.0]
    renderView1.CameraFocalPoint = [0.5, 0.5, 0.0]
    renderView1.CameraViewUp = [0.0, 1.0, 0.0]

    # set scalar coloring
    ColorBy(outeDisplay, ('FIELD', 'vtkBlockColors'))

    # show color bar/color legend
    outeDisplay.SetScalarBarVisibility(renderView1, True)

    # get color transfer function/color map for 'vtkBlockColors'
    vtkBlockColorsLUT = GetColorTransferFunction('vtkBlockColors')
    vtkBlockColorsLUT.InterpretValuesAsCategories = 1
    vtkBlockColorsLUT.Annotations = ['0', '0', '1', '1', '2', '2', '3', '3', '4', '4', '5', '5', '6', '6', '7', '7', '8', '8', '9', '9', '10', '10', '11', '11']
    vtkBlockColorsLUT.ActiveAnnotatedValues = ['0']
    vtkBlockColorsLUT.IndexedColors = [1.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 0.0, 1.0, 0.0, 1.0, 0.0, 1.0, 1.0, 0.63, 0.63, 1.0, 0.67, 0.5, 0.33, 1.0, 0.5, 0.75, 0.53, 0.35, 0.7, 1.0, 0.75, 0.5]

    # get opacity transfer function/opacity map for 'vtkBlockColors'
    vtkBlockColorsPWF = GetOpacityTransferFunction('vtkBlockColors')

    # set scalar coloring
    ColorBy(outeDisplay, ('POINTS', 'vel_'))

    # Hide the scalar bar for this color map if no visible data is colored by it.
    HideScalarBarIfNotNeeded(vtkBlockColorsLUT, renderView1)

    # rescale color and/or opacity maps used to include current data range
    outeDisplay.RescaleTransferFunctionToDataRange(True, False)

    # show color bar/color legend
    outeDisplay.SetScalarBarVisibility(renderView1, True)

    # get color transfer function/color map for 'vel_'
    vel_LUT = GetColorTransferFunction('vel_')
    vel_LUT.RGBPoints = [0.0, 0.231373, 0.298039, 0.752941, 0.0, 0.865003, 0.865003, 0.865003, 0.0, 0.705882, 0.0156863, 0.14902]
    vel_LUT.ScalarRangeInitialized = 1.0

    # get opacity transfer function/opacity map for 'vel_'
    vel_PWF = GetOpacityTransferFunction('vel_')
    vel_PWF.Points = [0.0, 0.0, 0.5, 0.0, 0.0, 1.0, 0.5, 0.0]
    vel_PWF.ScalarRangeInitialized = 1

    # create a new 'Plot Over Line'
    plotOverLine1 = PlotOverLine(Input=oute,
        Source='High Resolution Line Source')

    # init the 'High Resolution Line Source' selected for 'Source'
    plotOverLine1.Source.Point2 = [1.0, 1.0, 0.0]

    # Properties modified on plotOverLine1.Source
    plotOverLine1.Source.Point1 = [0.5, 0.0, 0.0]
    plotOverLine1.Source.Point2 = [0.5, 1.0, 0.0]

    # Properties modified on plotOverLine1
    plotOverLine1.Tolerance = 2.22044604925031e-16

    # Properties modified on plotOverLine1.Source
    plotOverLine1.Source.Point1 = [0.5, 0.0, 0.0]
    plotOverLine1.Source.Point2 = [0.5, 1.0, 0.0]

    # Create a new 'Line Chart View'
    lineChartView1 = CreateView('XYChartView')
    lineChartView1.ViewSize = [801, 832]

    # get layout
    layout1 = GetLayout()

    # place view in the layout
    layout1.AssignView(2, lineChartView1)

    # show data in view
    plotOverLine1Display = Show(plotOverLine1, lineChartView1)
    # trace defaults for the display properties.
    plotOverLine1Display.CompositeDataSetIndex = [0]
    plotOverLine1Display.UseIndexForXAxis = 0
    plotOverLine1Display.XArrayName = 'arc_length'
    plotOverLine1Display.SeriesVisibility = ['p', 'vel__Magnitude']
    plotOverLine1Display.SeriesLabel = ['arc_length', 'arc_length', 'ObjectId', 'ObjectId', 'p', 'p', 'vel__X', 'vel__X', 'vel__Y', 'vel__Y', 'vel__Z', 'vel__Z', 'vel__Magnitude', 'vel__Magnitude', 'vtkValidPointMask', 'vtkValidPointMask', 'Points_X', 'Points_X', 'Points_Y', 'Points_Y', 'Points_Z', 'Points_Z', 'Points_Magnitude', 'Points_Magnitude']
    plotOverLine1Display.SeriesColor = ['arc_length', '0', '0', '0', 'ObjectId', '0.89', '0.1', '0.11', 'p', '0.22', '0.49', '0.72', 'vel__X', '0.3', '0.69', '0.29', 'vel__Y', '0.6', '0.31', '0.64', 'vel__Z', '1', '0.5', '0', 'vel__Magnitude', '0.65', '0.34', '0.16', 'vtkValidPointMask', '0', '0', '0', 'Points_X', '0.89', '0.1', '0.11', 'Points_Y', '0.22', '0.49', '0.72', 'Points_Z', '0.3', '0.69', '0.29', 'Points_Magnitude', '0.6', '0.31', '0.64']
    plotOverLine1Display.SeriesPlotCorner = ['arc_length', '0', 'ObjectId', '0', 'p', '0', 'vel__X', '0', 'vel__Y', '0', 'vel__Z', '0', 'vel__Magnitude', '0', 'vtkValidPointMask', '0', 'Points_X', '0', 'Points_Y', '0', 'Points_Z', '0', 'Points_Magnitude', '0']
    plotOverLine1Display.SeriesLineStyle = ['arc_length', '1', 'ObjectId', '1', 'p', '1', 'vel__X', '1', 'vel__Y', '1', 'vel__Z', '1', 'vel__Magnitude', '1', 'vtkValidPointMask', '1', 'Points_X', '1', 'Points_Y', '1', 'Points_Z', '1', 'Points_Magnitude', '1']
    plotOverLine1Display.SeriesLineThickness = ['arc_length', '2', 'ObjectId', '2', 'p', '2', 'vel__X', '2', 'vel__Y', '2', 'vel__Z', '2', 'vel__Magnitude', '2', 'vtkValidPointMask', '2', 'Points_X', '2', 'Points_Y', '2', 'Points_Z', '2', 'Points_Magnitude', '2']
    plotOverLine1Display.SeriesMarkerStyle = ['arc_length', '0', 'ObjectId', '0', 'p', '0', 'vel__X', '0', 'vel__Y', '0', 'vel__Z', '0', 'vel__Magnitude', '0', 'vtkValidPointMask', '0', 'Points_X', '0', 'Points_Y', '0', 'Points_Z', '0', 'Points_Magnitude', '0']
    plotOverLine1Display.SeriesLabelPrefix = ''

    # destroy lineChartView1
    Delete(lineChartView1)
    del lineChartView1

    # close an empty frame
    layout1.Collapse(2)

    # set active view
    SetActiveView(renderView1)

    writer = CreateWriter("./rde/" + str(time_step) + "/original_data_from_extractor.csv", plotOverLine1)
    writer.FieldAssociation = "Points" # or "Cells"
    writer.UpdatePipeline()

    # clean original extracted raw data from exodus file
    with open("./rde/" + str(time_step) + "/original_data_from_extractor.csv", "r") as input_file, open("./rde/" + str(time_step) + "/extractor_" + str(time_step) + ".data", "w+") as output_file:
        header = True
        
        for line in input_file:
            if(header):
                output_file.write("filename;timestep;time;u;v;w;p;x;y;z")
                header = False
            else:
                line = line.replace(",",";").replace("\n","")
                splitted_line = line.split(";")
                output_file.write("\n" + ";".join([ 
                    "\"" + os.getcwd() + "/rde/" + str(time_step) + "/extractor_" + str(time_step) + ".data\"", str(time_step), str(timestep_values[time_step - 1]),
                    splitted_line[0], splitted_line[1], splitted_line[2], 
                    splitted_line[3], splitted_line[7], splitted_line[8], splitted_line[9]]))
                output_file.flush()
        
        output_file.close()
        input_file.close()
