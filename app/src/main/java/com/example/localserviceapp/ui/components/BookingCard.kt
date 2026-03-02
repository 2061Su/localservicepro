package com.example.localserviceapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.localserviceapp.data.model.Booking
import com.example.localserviceapp.data.model.BookingStatus
import com.example.localserviceapp.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun BookingCard(
    booking: Booking,
    isAdmin: Boolean,
    onApprove: () -> Unit = {},
    onReject: () -> Unit = {},
    onDelete: () -> Unit = {},
    onEdit: () -> Unit = {},
    onCancel: () -> Unit = {}
) {
    val dateStr = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(booking.selectedDate))
    
    PremiumCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = booking.serviceName, style = MaterialTheme.typography.titleLarge)
                PremiumStatusBadge(status = booking.status)
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = "Scheduled for: $dateStr", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            
            if (isAdmin) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Customer: ${booking.userName}", style = MaterialTheme.typography.bodySmall)
                Text(text = "Loc: ${booking.userLocation}", style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                if (isAdmin) {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, "Edit")
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, "Delete", tint = MaterialTheme.colorScheme.error)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = onApprove, colors = ButtonDefaults.buttonColors(containerColor = EmeraldText)) {
                        Text("Approve")
                    }
                } else if (booking.status == BookingStatus.PENDING.name) {
                    TextButton(onClick = onEdit) { Text("Reschedule") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = onCancel, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                        Text("Cancel")
                    }
                }
            }
        }
    }
}

@Composable
fun PremiumStatusBadge(status: String) {
    val (backgroundColor, textColor) = when (status) {
        BookingStatus.APPROVED.name -> SoftEmerald to EmeraldText
        BookingStatus.REJECTED.name -> SoftRose to RoseText
        BookingStatus.CANCELLED.name -> Color.LightGray.copy(0.2f) to Color.DarkGray
        else -> SoftAmber to AmberText
    }
    
    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = status,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            color = textColor
        )
    }
}
