package com.example.vphone.ui.performance

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardSurface
import com.example.ui.theme.CardSurfaceVariant
import com.example.ui.theme.CyberPink
import com.example.ui.theme.DarkCharcoal
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSubtle
import com.example.ui.theme.TextWhite
import com.example.ui.theme.VoidBlack
import com.example.ui.theme.WarningAmber
import com.example.vphone.engine.FeasibilityReport
import com.example.vphone.ui.components.GamingBadge

@Composable
fun FeasibilityReportDialog(
    report: FeasibilityReport,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, CardBorder, RoundedCornerShape(20.dp)),
            color = CardSurface
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "HARDWARE FEASIBILITY REPORT",
                            color = TextWhite,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "Android Virtualization & 144 FPS Engine Diagnostic",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.testTag("feasibility_close_button")) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Target Device Spec
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    color = DarkCharcoal,
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = "TARGET PHYSICAL HARDWARE", color = TextSubtle, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text(text = "${report.physicalManufacturer} ${report.physicalModel}", color = NeonCyan, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Text(text = report.physicalOsVersion, color = TextWhite, fontSize = 11.sp)
                        Text(text = "Total System RAM: ${report.totalRamMb} MB (Safe Allocatable: ${report.safeAllocatableRamMb} MB)", color = TextMuted, fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Diagnostic Checklist
                Text(text = "VIRTUALIZATION SUBSYSTEM AUDIT", color = TextSubtle, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                AuditCheckRow(
                    title = "Android Virtualization Framework (AVF)",
                    status = report.avfClassFound,
                    positiveText = "VirtualMachineManager class present",
                    negativeText = "AVF not exposed to 3rd-party user apps"
                )
                AuditCheckRow(
                    title = "Kernel KVM Node (/dev/kvm)",
                    status = report.kvmDevicePresent,
                    positiveText = "/dev/kvm node present in kernel",
                    negativeText = "Hypervisor node restricted by OEM SELinux"
                )
                AuditCheckRow(
                    title = "Protected KVM (pKVM) Support",
                    status = report.kernelPkvmSupported,
                    positiveText = "pKVM micro-kernel verified",
                    negativeText = "Hardware pKVM restricted to OEM firmware"
                )
                AuditCheckRow(
                    title = "3D GPU Acceleration Passthrough",
                    status = report.gpuVirtSupported,
                    positiveText = "virtio-gpu / Venus Vulkan active",
                    negativeText = "Direct Host GPU Passthrough (Container Mode)"
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Technical Verdict
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = CardSurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ENGINE ARCHITECTURAL VERDICT",
                                color = NeonCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = report.technicalVerdict,
                            color = TextWhite,
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Why this is optimal for 144 FPS Gaming:",
                            color = NeonGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "1. Zero GPU Emulation Overhead: Games talk directly to the physical ARM Mali/Adreno GPU with native Vulkan 1.3 speed.\n2. 100% Anti-Cheat Safety: Tencent ACE, Krafton MTP & Garena HackShield detect hypervisors and ban players. Container sandboxing passes Play Integrity safely.\n3. Native 144Hz VSync: Android window attributes enforce exact 144Hz timing directly.",
                            color = TextMuted,
                            fontSize = 10.sp,
                            lineHeight = 15.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "ACKNOWLEDGE & CLOSE", color = VoidBlack, fontSize = 12.sp, fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

@Composable
fun AuditCheckRow(
    title: String,
    status: Boolean,
    positiveText: String,
    negativeText: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Icon(
                imageVector = if (status) Icons.Default.CheckCircle else Icons.Default.Cancel,
                contentDescription = null,
                tint = if (status) NeonGreen else WarningAmber,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(text = title, color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                Text(text = if (status) positiveText else negativeText, color = TextMuted, fontSize = 10.sp)
            }
        }
        GamingBadge(
            text = if (status) "READY" else "RESTRICTED",
            color = if (status) NeonGreen else WarningAmber
        )
    }
}
