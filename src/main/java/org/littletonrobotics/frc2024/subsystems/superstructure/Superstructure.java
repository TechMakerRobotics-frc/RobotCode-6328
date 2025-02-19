// Copyright (c) 2024 FRC 6328
// http://github.com/Mechanical-Advantage
//
// Use of this source code is governed by an MIT-style
// license that can be found in the LICENSE file at
// the root directory of this project.

package org.littletonrobotics.frc2024.subsystems.superstructure;

import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.littletonrobotics.frc2024.subsystems.superstructure.arm.Arm;
import org.littletonrobotics.frc2024.subsystems.superstructure.backpackactuator.BackpackActuator;
import org.littletonrobotics.frc2024.subsystems.superstructure.climber.Climber;
import org.littletonrobotics.junction.AutoLogOutput;
import org.littletonrobotics.junction.Logger;

public class Superstructure extends SubsystemBase {

  @NoArgsConstructor(force = true)
  @RequiredArgsConstructor
  @Getter
  public enum Goal {
    STOW,
    BACKPACK_OUT_UNJAM,
    AIM,
    SUPER_POOP,
    UNJAM_FEEDER,
    STATION_INTAKE,
    AMP,
    SUBWOOFER,
    PODIUM,
    RESET_CLIMB(true),
    PREPARE_PREPARE_TRAP_CLIMB(true),
    PREPARE_CLIMB(true),
    POST_PREPARE_TRAP_CLIMB(true),
    CLIMB(true),
    TRAP(true),
    UNTRAP(true),
    RESET,
    DIAGNOSTIC_ARM;

    private final boolean climbingGoal;
  }

  @Getter private Goal currentGoal = Goal.STOW;
  @Getter private Goal desiredGoal = Goal.STOW;
  private Goal lastGoal = Goal.STOW;

  private final Arm arm;
  private final Climber climber;
  private final BackpackActuator backpackActuator;

  private Timer goalTimer = new Timer();

  public Superstructure(Arm arm, Climber climber, BackpackActuator backpackActuator) {
    this.arm = arm;
    this.climber = climber;
    this.backpackActuator = backpackActuator;

    setDefaultCommand(setGoalCommand(Goal.STOW));
    goalTimer.start();
  }

  @Override
  public void periodic() {
    if (DriverStation.isDisabled()) {
      setDefaultCommand(setGoalCommand(Goal.STOW));
      arm.stop();
    }

    // Retract climber
    if (!climber.retracted()
        && !desiredGoal.isClimbingGoal()
        && !DriverStation.isAutonomousEnabled()) {
      currentGoal = Goal.RESET_CLIMB;
    } else {
      currentGoal = desiredGoal;
    }

    // Reset timer
    if (currentGoal != lastGoal) {
      goalTimer.reset();
    }
    lastGoal = currentGoal;

    switch (currentGoal) {
      case STOW -> {
        arm.setGoal(Arm.Goal.STOW);
        climber.setGoal(Climber.Goal.IDLE);
        backpackActuator.setGoal(BackpackActuator.Goal.RETRACT);
      }
      case BACKPACK_OUT_UNJAM -> {
        arm.setGoal(Arm.Goal.UNJAM_INTAKE);
        climber.setGoal(Climber.Goal.IDLE);
        backpackActuator.setGoal(BackpackActuator.Goal.EXTEND);
      }
      case UNJAM_FEEDER -> {
        arm.setGoal(Arm.Goal.UNJAM_INTAKE);
        climber.setGoal(Climber.Goal.IDLE);
        backpackActuator.setGoal(BackpackActuator.Goal.RETRACT);
      }
      case AIM -> {
        arm.setGoal(Arm.Goal.AIM);
        climber.setGoal(Climber.Goal.IDLE);
        backpackActuator.setGoal(BackpackActuator.Goal.RETRACT);
      }
      case SUPER_POOP -> {
        arm.setGoal(Arm.Goal.SUPER_POOP);
        climber.setGoal(Climber.Goal.IDLE);
        backpackActuator.setGoal(BackpackActuator.Goal.RETRACT);
      }
      case STATION_INTAKE -> {
        arm.setGoal(Arm.Goal.STATION_INTAKE);
        climber.setGoal(Climber.Goal.IDLE);
        backpackActuator.setGoal(BackpackActuator.Goal.RETRACT);
      }
      case AMP -> {
        arm.setGoal(Arm.Goal.AMP);
        climber.setGoal(Climber.Goal.IDLE);
        backpackActuator.setGoal(BackpackActuator.Goal.RETRACT);
      }
      case SUBWOOFER -> {
        arm.setGoal(Arm.Goal.SUBWOOFER);
        climber.setGoal(Climber.Goal.IDLE);
        backpackActuator.setGoal(BackpackActuator.Goal.RETRACT);
      }
      case PODIUM -> {
        arm.setGoal(Arm.Goal.PODIUM);
        climber.setGoal(Climber.Goal.IDLE);
        backpackActuator.setGoal(BackpackActuator.Goal.RETRACT);
      }
      case RESET_CLIMB -> {
        arm.setGoal(Arm.Goal.RESET_CLIMB);
        if (arm.atGoal()) {
          // Retract and then stop
          climber.setGoal(Climber.Goal.IDLE);
        } else {
          // Arm in unsafe state to retract, apply no current
          climber.setGoal(Climber.Goal.STOP);
        }
        backpackActuator.setGoal(BackpackActuator.Goal.RETRACT);
      }
      case PREPARE_PREPARE_TRAP_CLIMB -> {
        arm.setGoal(Arm.Goal.PREPARE_PREPARE_TRAP_CLIMB);
        climber.setGoal(Climber.Goal.EXTEND);
        backpackActuator.setGoal(BackpackActuator.Goal.RETRACT);
      }
      case PREPARE_CLIMB -> {
        arm.setGoal(Arm.Goal.PREPARE_CLIMB);
        climber.setGoal(Climber.Goal.EXTEND);
        backpackActuator.setGoal(BackpackActuator.Goal.RETRACT);
      }
      case POST_PREPARE_TRAP_CLIMB -> {
        arm.setGoal(Arm.Goal.CLIMB);
        climber.setGoal(Climber.Goal.EXTEND);
        backpackActuator.setGoal(BackpackActuator.Goal.RETRACT);
      }
      case CLIMB -> {
        arm.setGoal(Arm.Goal.CLIMB);
        climber.setGoal(Climber.Goal.RETRACT);
        backpackActuator.setGoal(BackpackActuator.Goal.RETRACT);
      }
      case TRAP -> {
        arm.setGoal(Arm.Goal.CLIMB);
        climber.setGoal(Climber.Goal.RETRACT);
        backpackActuator.setGoal(BackpackActuator.Goal.EXTEND);
      }
      case UNTRAP -> {
        arm.setGoal(Arm.Goal.UNTRAP);
        climber.setGoal(Climber.Goal.RETRACT);
        if (goalTimer.hasElapsed(0.1)) {
          backpackActuator.setGoal(BackpackActuator.Goal.RETRACT);
        } else {
          backpackActuator.setGoal(BackpackActuator.Goal.EXTEND);
        }
      }
      case RESET -> {
        desiredGoal = Goal.STOW;
        setDefaultCommand(setGoalCommand(Goal.STOW));
      }
      case DIAGNOSTIC_ARM -> {
        arm.setGoal(Arm.Goal.CUSTOM);
        climber.setGoal(Climber.Goal.IDLE);
        backpackActuator.setGoal(BackpackActuator.Goal.RETRACT);
      }
    }

    arm.periodic();
    climber.periodic();
    backpackActuator.periodic();

    Logger.recordOutput("Superstructure/GoalState", desiredGoal);
    Logger.recordOutput("Superstructure/CurrentState", currentGoal);
  }

  /** Set goal of superstructure */
  private void setGoal(Goal goal) {
    if (desiredGoal == goal) return;
    desiredGoal = goal;
  }

  /** Command to set goal of superstructure */
  public Command setGoalCommand(Goal goal) {
    return startEnd(() -> setGoal(goal), () -> setGoal(Goal.STOW))
        .withName("Superstructure " + goal);
  }

  /** Command to set goal of superstructure with additional profile constraints on arm */
  public Command setGoalWithConstraintsCommand(
      Goal goal, TrapezoidProfile.Constraints armProfileConstraints) {
    return setGoalCommand(goal)
        .beforeStarting(() -> arm.setProfileConstraints(armProfileConstraints))
        .finallyDo(() -> arm.setProfileConstraints(Arm.maxProfileConstraints.get()));
  }

  /** Command to aim the superstructure with a compensation value in degrees */
  public Command aimWithCompensation(double compensation) {
    return setGoalCommand(Goal.AIM)
        .beforeStarting(() -> arm.setCurrentCompensation(compensation))
        .finallyDo(() -> arm.setCurrentCompensation(0.0));
  }

  @AutoLogOutput(key = "Superstructure/CompletedGoal")
  public boolean atGoal() {
    return currentGoal == desiredGoal && arm.atGoal() && climber.atGoal();
  }

  @AutoLogOutput(key = "Superstructure/AtArmGoal")
  public boolean atArmGoal() {
    return currentGoal == desiredGoal && arm.atGoal();
  }

  public void runArmCharacterization(double input) {
    arm.runCharacterization(input);
  }

  public double getArmCharacterizationVelocity() {
    return arm.getCharacterizationVelocity();
  }

  public void endArmCharacterization() {
    arm.endCharacterization();
  }
}
