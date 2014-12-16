package com.egnyte.fbplugins.deprecated3rdpartyrules;

import java.util.List;

import lombok.Getter;

import com.egnyte.fbplugins.deprecated3rdpartyrules.model.deprecation.Deprecation;
import com.egnyte.fbplugins.settings.SetupChecker;

import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.bcel.OpcodeStackDetector;

@Getter
public class Deprecated3rdPartyDetector extends OpcodeStackDetector {
    public static final String DETECTOR_NAME = Deprecated3rdPartyDetector.class.getSimpleName();

    private final BugReporter bugReporter;

    private final List<Deprecation> deprecationList;

    public Deprecated3rdPartyDetector(BugReporter bugReporter) throws Exception {
        this.bugReporter = bugReporter;
        this.deprecationList = new SetupChecker().getSettings().getDeprecatedSettings().getDeprecations();
    }

    @Override
    public void sawOpcode(int seen) {
        Deprecated3dPartyChecker checker = new Deprecated3dPartyChecker();

        try {
            String refConstantOperand = this.getRefConstantOperand();

            for (Deprecation deprecation : deprecationList) {
                deprecation.accept(checker, this, refConstantOperand);
            }
        } catch (IllegalStateException e) {
            // This method throws IllegalStateException in the case that ref
            // constant operand is not available for the current opcode.
            // We need to interpret opcodes with ref constant operand only.
        }
    }
}
